package models

import play.api.Play.current
import play.api.db.DB
import anorm._
import anorm.SqlParser._
import anorm.~
import play.Logger
import scala.util.Random

case class Relationship(parent: Option[Parent], child: Option[ChildInfo], card: String, relationship: String, id: Option[Long] = None)

case class RelationshipIdentity(id: Long, card: String)

object Relationship {
  def search(cardNumber: String) = DB.withConnection {
    implicit c =>
      SQL("select * from relationmap where card_num={card}").on('card -> cardNumber).as(searchSample singleOpt)

  }

  def fakeCardCreate(kg: Long, relationship: String, phone: String, childId: String) = DB.withTransaction {
    implicit c =>
      val random: Random = new Random(System.currentTimeMillis)
      try {
        val id: Option[Long] = SQL("insert into relationmap (child_id, parent_id, card_num, relationship, reference_id) " +
          " select {child_id}, (select parent_id from parentinfo where phone={phone} and school_id={kg}), (SELECT concat('F', LPAD(count(1), 9, '0')) FROM relationmap), {relationship}, {reference_id}")
          .on(
            'phone -> phone,
            'child_id -> childId,
            'relationship -> relationship,
            'kg -> kg.toString,
            'reference_id -> "%s_%s_%s".format(childId, phone, random.nextString(4))
          ).executeInsert()
        c.commit()
        findById(kg)(id.getOrElse(-1))
      }
      catch {
        case e: Throwable =>
          c.rollback()
          Logger.info("create no card relationship error...")
          Logger.info(e.getLocalizedMessage)
          None
      }
  }

  def updateCardNumber(kg: Long, oldCard: String, newCard: String, phone: String, childId: String) = DB.withConnection {
    implicit c =>
      SQL("update relationmap set card_num={newCard}" +
        "where card_num={old} " +
        "and parent_id=(select parent_id from parentinfo where phone={phone} and school_id={kg}) " +
        "and child_id={child_id}")
        .on(
          'phone -> phone,
          'child_id -> childId,
          'kg -> kg.toString,
          'newCard -> newCard,
          'old -> oldCard
        ).executeUpdate()
      show(kg, newCard)
  }


  def update(kg: Long, card: String, relationship: String, phone: String, childId: String, id: Long) = DB.withConnection {
    implicit c =>
      SQL("update relationmap set child_id={child_id}, " +
        "parent_id=(select parent_id from parentinfo where phone={phone} and school_id={kg}), " +
        "relationship={relationship}, card_num={card} " +
        "where uid={id}")
        .on(
          'phone -> phone,
          'child_id -> childId,
          'relationship -> relationship,
          'kg -> kg.toString,
          'card -> card,
          'id -> id
        ).executeUpdate()
      show(kg, card)
  }

  def cardExists(card: String, id: Option[Long]) = DB.withConnection {
    implicit c =>
      var sql: String = "select count(1) from relationmap where status=1 and card_num={card}"
      id map (x => sql = sql + " and uid={id}")
      SQL(sql).on('card -> card, 'id -> id).as(get[Long]("count(1)") single) > 0

  }

  val relationshipId = {
    get[Long]("uid") ~
      get[String]("card_num") map {
      case id ~ card =>
        RelationshipIdentity(id, card)
    }
  }

  def getCard(phone: String, childId: String) = DB.withConnection {
    implicit c =>
      val option = SQL("select uid, card_num from relationmap where status=1 and child_id={childId} " +
        "and parent_id=(select parent_id from parentinfo where phone={phone} limit 1)")
        .on(
          'phone -> phone,
          'childId -> childId
        ).as(relationshipId singleOpt)
      Logger.info(option.toString)
      option
  }

  def getChildIdByCard(card: String) = DB.withConnection {
    implicit c =>
      val option = SQL("select child_id from relationmap where status=1 and card_num={card} limit 1")
        .on('card -> card).as(get[String]("child_id") singleOpt)
      Logger.info(s"find child ${option.toString} by $card")
      option
  }

  def delete(kg: Long, card: String) = DB.withConnection {
    implicit c =>
      SQL("delete from relationmap where card_num={card}")
        .on('card -> card
        ).execute()
  }

  def show(kg: Long, card: String) = DB.withConnection {
    implicit c =>
      SQL("select * from relationmap where card_num={card}").on('card -> card).as(simple(kg) singleOpt)
  }


  def findById(kg: Long)(uid: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from relationmap where uid={uid}").on('uid -> uid).as(simple(kg) singleOpt)
  }


  def create(kg: Long, card: String, relationship: String, phone: String, childId: String) = DB.withTransaction {
    implicit c =>
      val random: Random = new Random(System.currentTimeMillis)
      try {
        val id: Option[Long] = SQL("insert into relationmap (child_id, parent_id, card_num, relationship, reference_id) VALUES" +
          " ({child_id}, (select parent_id from parentinfo where phone={phone} and school_id={kg}), {card}, {relationship}, {reference_id})")
          .on(
            'phone -> phone,
            'child_id -> childId,
            'relationship -> relationship,
            'kg -> kg.toString,
            'card -> card,
            'reference_id -> "%s_%s_%s".format(childId, phone, random.nextString(4))
          ).executeInsert()
        c.commit()
        findById(kg)(id.getOrElse(-1))
      }
      catch {
        case e: Throwable =>
          c.rollback()
          Logger.info("create relationship error...")
          Logger.info(e.getLocalizedMessage)
          None
      }
  }


  def simple(kg: Long) = {
    get[Long]("uid") ~
      get[String]("parent_id") ~
      get[String]("child_id") ~
      get[String]("card_num") ~
      get[String]("relationship") map {
      case id ~ parent ~ child ~ cardNum ~ r =>
        Relationship(Parent.info(kg, parent), Children.info(kg, child), cardNum, r, Some(id))
    }
  }

  val searchSample = {
    get[Long]("uid") ~
      get[String]("parent_id") ~
      get[String]("child_id") ~
      get[String]("card_num") ~
      get[String]("relationship") map {
      case id ~ parent ~ child ~ cardNum ~ r =>
        Relationship(Parent.idSearch(parent), Children.idSearch(child), cardNum, r, Some(id))
    }
  }

  def index(kg: Long, parent: Option[String], child: Option[String], classId: Option[Long]) = DB.withConnection {
    implicit c =>
      val query: String = generateQuery(parent, child, classId)
      Logger.info(query)
      SQL(query)
        .on(
          'kg -> kg.toString,
          'phone -> parent,
          'child_id -> child,
          'class_id -> classId
        ).as(simple(kg) *)
  }


  def generateQuery(parent: Option[String], child: Option[String], classId: Option[Long]) = {
    var sql = "select r.* from relationmap r, childinfo c, parentinfo p where r.child_id=c.child_id and p.parent_id=r.parent_id" +
      " and p.school_id={kg} and p.status=1 and r.status=1 and p.school_id=c.school_id and c.status=1 "
    val morethanOneChild = "^([\\w_]+)(,[\\w_]+)*$".r
    val oneChild = "^([\\w_]+)$".r
    parent foreach {
      phone =>
        sql += " and p.phone={phone}"
    }
    child foreach {
      case oneChild(one) =>
        sql += " and r.child_id={child_id}"
      case morethanOneChild(args @ _*) =>
        sql += s" and r.child_id in ('${child.get.split(",").mkString("','")}')"
    }
    classId foreach {
      child_id =>
        sql += " and c.class_id={class_id}"
    }
    sql
  }
}
