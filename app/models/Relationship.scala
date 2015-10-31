package models

import java.sql.Connection
import java.util.Date

import models.helper.MD5Helper._
import play.api.Play.current
import play.api.db.DB
import anorm._
import anorm.SqlParser._
import anorm.~
import play.Logger
import play.api.libs.json.Json
import scala.util.Random
import models.helper.TimeHelper.any2DateTime

case class Relationship(parent: Option[Parent], child: Option[ChildInfo], card: String, relationship: String, id: Option[Long] = None)

case class RelationshipIdentity(id: Long, card: String)

object Relationship {
  implicit val writeParent = Json.writes[Parent]
  implicit val writeChildInfo = Json.writes[ChildInfo]
  implicit val writeRelationship = Json.writes[Relationship]

  def deleted(card: String): Boolean = DB.withConnection {
    implicit c =>
      SQL("select count(1) from relationmap where card_num={card} and status=0")
        .on('card -> card)
        .as(get[Long]("count(1)") single) > 0
  }

  def search(cardNumber: String) = DB.withConnection {
    implicit c =>
      SQL("select * from relationmap where card_num={card}").on('card -> cardNumber).as(searchSample singleOpt)

  }

  def fakeCardCreate(kg: Long, relationship: String, phone: String, childId: String) = DB.withTransaction {
    implicit c =>
      val random: Random = new Random(System.currentTimeMillis)
      val fakeCardNumber: String = "f" + md5(s"${phone}_$childId").take(19)
      try {
        val id: Option[Long] = SQL("insert into relationmap (child_id, parent_id, card_num, relationship, reference_id) " +
          " select {child_id}, (select parent_id from parentinfo where phone={phone} and school_id={kg}), {card}, {relationship}, {reference_id}")
          .on(
            'phone -> phone,
            'child_id -> childId,
            'relationship -> relationship,
            'card -> fakeCardNumber,
            'kg -> kg.toString,
            'reference_id -> md5(s"${childId}_${phone}_${random.nextString(4)}").take(40)
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

  def reuseDeletedCard(kg: Long, card: String, relationship: String, phone: String, childId: String) = DB.withConnection {
    implicit c =>
      SQL("update relationmap set child_id={child_id}, " +
        "parent_id=(select parent_id from parentinfo where phone={phone} and school_id={kg}), " +
        "relationship={relationship}, status=1 " +
        "where card_num={card} and status=0")
        .on(
          'phone -> phone,
          'child_id -> childId,
          'relationship -> relationship,
          'kg -> kg.toString,
          'card -> card
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

  def deleteCardByChildId(childId: String) = DB.withConnection {
    implicit c =>
      SQL("delete from relationmap where child_id={child}")
        .on('child -> childId
        ).execute()
  }

  def deleteCardByParentId(parentId: String) = DB.withConnection {
    implicit c =>
      SQL("delete from relationmap where parent_id={parent}")
        .on('parent -> parentId
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
          " ({child_id}, (select parent_id from parentinfo where phone={phone} and school_id={kg} limit 1), {card}, {relationship}, {reference_id})")
          .on(
            'phone -> phone,
            'child_id -> childId,
            'relationship -> relationship,
            'kg -> kg.toString,
            'card -> card,
            'reference_id -> md5(s"${childId}_${phone}_${random.nextString(4)}").take(40)
          ).executeInsert()
        c.commit()
        findById(kg)(id.getOrElse(-1))
      }
      catch {
        case e: Throwable =>
          c.rollback()
          Logger.warn("create relationship error...")
          Logger.warn(e.getLocalizedMessage)
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

  def fullRelationship(kg: Long) = {
    get[Long]("relationmap.uid") ~
      get[Long]("parentinfo.uid") ~
      get[Long]("childinfo.uid") ~
      get[String]("parentinfo.parent_id") ~
      get[String]("childinfo.child_id") ~
      get[String]("card_num") ~
      get[String]("relationmap.relationship") ~
      get[String]("parentinfo.name") ~
      get[String]("parentinfo.phone") ~
      get[Int]("parentinfo.gender") ~
      get[Option[String]]("parentinfo.picurl") ~
      get[Date]("parentinfo.birthday") ~
      get[Int]("member_status") ~
      get[Int]("parentinfo.status") ~
      get[Option[String]]("parentinfo.company") ~
      get[Long]("parentinfo.update_at") ~
      get[Long]("parentinfo.created_at") ~
      get[String]("childinfo.name") ~
      get[String]("nick") ~
      get[Option[String]]("childinfo.picurl") ~
      get[Int]("childinfo.gender") ~
      get[Date]("childinfo.birthday") ~
      get[Int]("childinfo.class_id") ~
      get[String]("classinfo.class_name") ~
      get[Option[String]]("childinfo.address") ~
      get[Long]("childinfo.update_at") ~
      get[Long]("childinfo.created_at") ~
      get[Int]("childinfo.status") map {
      case id ~ pId ~ cId ~ parent ~ child ~ cardNum ~ r ~ name ~ phone ~ pGender ~ pImage ~ pBirthday ~ member
        ~ pStatus ~ pCompany ~ pUpdated ~ pCreated ~ childName ~ nick ~ icon_url ~ childGender
        ~ childBirthday ~ classId ~ className ~ address ~ cUpdated ~ cCreated ~ cStatus =>
        Relationship(Some(Parent(Some(parent), kg, name, phone, Some(pImage.getOrElse("")), pGender, pBirthday.toDateOnly,
          Some(pUpdated), Some(member), Some(pStatus), pCompany, None, Some(pCreated), Some(pId))),
          Some(ChildInfo(Some(child), childName, nick, Some(childBirthday.toDateOnly), childGender,
            Some(icon_url.getOrElse("")), classId, Some(className), Some(cUpdated), Some(kg), address, Some(cStatus),
            Some(cCreated), Some(cId))), cardNum, r, Some(id))
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
      Logger.debug(s"Relationship index: $query")
      SQL(query)
        .on(
          'kg -> kg.toString,
          'phone -> parent,
          'child_id -> child,
          'class_id -> classId
        ).as(fullRelationship(kg) *)
  }


  def generateQuery(parent: Option[String], child: Option[String], classId: Option[Long]) = {
    var sql = "select * from relationmap r, childinfo c, parentinfo p, classinfo cl where r.child_id=c.child_id and p.parent_id=r.parent_id" +
      " and p.school_id={kg} and p.status=1 and r.status=1 and p.school_id=c.school_id and c.status=1 and cl.school_id=c.school_id and cl.class_id=c.class_id and cl.status=1"
    val morethanOneChild = "^([\\w_]+)(,[\\w_]+)*$".r
    val oneChild = "^([\\w_]+)$".r
    parent foreach {
      phone =>
        sql += " and p.phone={phone}"
    }
    child foreach {
      case oneChild(one) =>
        sql += " and r.child_id={child_id}"
      case morethanOneChild(args@_*) =>
        sql += s" and r.child_id in ('${child.get.split(",").mkString("','")}')"
    }
    classId foreach {
      child_id =>
        sql += " and c.class_id={class_id}"
    }
    sql
  }
}
