package models

import play.api.Play.current
import play.api.db.DB
import anorm._
import anorm.SqlParser._
import anorm.~
import play.Logger

case class Relationship(parent: Option[Parent], child: Option[ChildInfo], card: String, relationship: String)

object Relationship {

  def update(kg: Long, card: String, relationship: String, phone: String, childId: String) = DB.withConnection {
    implicit c =>
      SQL("update relationmap set child_id={child_id}, " +
        "parent_id=(select parent_id from parentinfo where phone={phone} and school_id={kg}), " +
        "relationship={relationship} " +
        "where card_num={card}")
        .on(
          'phone -> phone,
          'child_id -> childId,
          'relationship -> relationship,
          'kg -> kg.toString,
          'card -> card
        ).executeUpdate()
      show(kg, card)
  }

  def cardExists(card: String) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from relationmap where status=1 and card_num={card}")
        .on('card -> card).as(get[Long]("count(1)") single) > 0
  }

  def getCard(phone: String, childId: String) = DB.withConnection {
    implicit c =>
      val option = SQL("select card_num from relationmap where status=1 and child_id={childId} " +
        "and parent_id=(select parent_id from parentinfo where phone={phone} limit 1)")
        .on(
          'phone -> phone,
          'childId -> childId
        ).as(get[String]("card_num") singleOpt)
      Logger.info(option.toString)
      option
  }

  def delete(kg: Long, card: String) = DB.withConnection {
    implicit c =>
      SQL("delete from relationmap where card_num={card}")
        .on('card -> card
        ).executeUpdate
  }

  def show(kg: Long, card: String) = DB.withConnection {
    implicit c =>
      SQL("select * from relationmap where card_num={card}").on('card -> card).as(simple(kg) singleOpt)
  }


  def findById(kg: Long)(uid: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from relationmap where uid={uid}").on('uid -> uid).as(simple(kg) singleOpt)
  }


  def create(kg: Long, card: String, relationship: String, phone: String, childId: String) = DB.withConnection {
    implicit c =>
      val id: Option[Long] = SQL("insert into relationmap (child_id, parent_id, card_num, relationship) VALUES" +
        " ({child_id}, (select parent_id from parentinfo where phone={phone} and school_id={kg}), {card}, {relationship})")
        .on(
          'phone -> phone,
          'child_id -> childId,
          'relationship -> relationship,
          'kg -> kg.toString,
          'card -> card
        ).executeInsert()
      findById(kg)(id.getOrElse(-1))
  }


  def simple(kg: Long) = {
    get[String]("parent_id") ~
      get[String]("child_id") ~
      get[String]("card_num") ~
      get[String]("relationship") map {
      case parent ~ child ~ cardNum ~ r =>
        Relationship(Parent.info(kg, parent), Children.info(kg, child), cardNum, r)
    }
  }

  def index(kg: Long, parent: Option[String], child: Option[String], classId: Option[Long]) = DB.withConnection {
    implicit c =>
      SQL(generateQuery(parent, child, classId))
        .on(
          'kg -> kg.toString,
          'phone -> parent,
          'child_id -> child,
          'class_id -> classId
        ).as(simple(kg) *)
  }


  def generateQuery(parent: Option[String], child: Option[String], classId: Option[Long]) = {
    var sql = "select r.* from relationmap r, childinfo c, parentinfo p where r.child_id=c.child_id and p.parent_id=r.parent_id" +
      " and p.school_id={kg} and p.status=1 and r.status=1 and p.school_id=c.school_id"
    parent map {
      phone =>
        sql += " and p.phone={phone}"
    }
    child map {
      child_id =>
        sql += " and c.child_id={child_id}"
    }
    classId map {
      child_id =>
        sql += " and c.class_id={class_id}"
    }
    sql
  }
}
