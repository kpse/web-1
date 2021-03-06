package models

import anorm.SqlParser._
import anorm._
import models.helper.MD5Helper._
import play.api.db.DB
import play.api.Play.current
import play.Logger
import play.api.libs.json.Json

case class BatchImportReport(id: String, reason: String)

case class ImportedParent(id: String, school_id: Long, name: String, phone: String, gender: Int, portrait: Option[String], birthday: String, member_status: Option[Int], status: Option[Int], company: Option[String] = None, timestamp: Option[Long]) {
  def exists = DB.withConnection {
    implicit c =>
      SQL("select count(1) from parentinfo where parent_id={id}").on('id -> id).as(get[Long]("count(1)") single) > 0
  }

  def transform = {
    Parent(Some(id), school_id, name, phone, portrait, gender, birthday, None, member_status, status, company, None, timestamp)
  }

  def importing = exists match {
    case true => update
    case false => create
  }

  def create = {
    val parent = transform.copy(status = Some(1))
    val create1: Option[Parent] = Parent.create(parent.school_id, parent)
    create1 match {
      case Some(x) =>
        None
      case None =>
        Some(BatchImportReport(parent.parent_id.getOrElse(""), s"家长 ${parent.parent_id} 手机号 ${parent.phone} 创建失败。"))
    }
  }

  def update = {
    Parent.update(transform.copy(status = Some(1)))
    None
  }
}

case class ImportedChild(id: String, name: String, nick: String, birthday: Option[String],
                         gender: Int, portrait: Option[String], class_id: Int,
                         timestamp: Option[Long], school_id: Option[Long], address: Option[String] = None, status: Option[Int] = Some(1)) {
  def transform = {
    ChildInfo(Some(id), name, nick, birthday, gender, portrait, class_id, None, timestamp, school_id, address, status, timestamp)
  }

  def exists = DB.withConnection {
    implicit c =>
      SQL("select count(1) from childinfo where child_id={id}").on('id -> id).as(get[Long]("count(1)") single) > 0
  }

  def importing = exists match {
    case true => update
    case false => create
  }

  def create = {
    val child = transform
    val create1: Option[ChildInfo] = Children.create(child.school_id.getOrElse(0), child)
    create1 match {
      case Some(x) =>
        None
      case None =>
        Some(BatchImportReport(child.child_id.getOrElse(""), s"学生 ${child.name} ${child.child_id} 创建失败。"))
    }
  }

  def update = {
    Children.update(school_id.get, transform.copy(status = Some(1)))
    None
  }
}

case class IdItem(id: String)

case class ImportedRelationship(id: String, card: String, parent: IdItem, child: IdItem, relationship: String) {
  def exists = DB.withConnection {
    implicit c =>
      SQL("select count(1) from relationmap where reference_id={id}").on('id -> id).as(get[Long]("count(1)") single) > 0
  }

  def importing = exists match {
    case true => update
    case false => create
  }

  def create = DB.withTransaction {
    implicit c =>
      val cardNumber: String = fakeCardIfNeeded
      Logger.info(s"cardNumber = $cardNumber")
      try {
        SQL("insert into relationmap (child_id, parent_id, card_num, relationship, reference_id) VALUES" +
          " ({child}, {parent}, {card}, {relationship}, {id})")
          .on(
            'parent -> parent.id,
            'child -> child.id,
            'relationship -> relationship,
            'card -> cardNumber,
            'id -> id
          ).executeInsert()
        c.commit()
        None
      }
      catch {
        case e: Throwable =>
          Logger.warn(e.getLocalizedMessage)
          c.rollback()
          Some(BatchImportReport(id, "卡号 %s（%s <-> %s）创建失败。".format(card, parent.id, child.id)))
      }
  }

  def fakeCardIfNeeded: String = {
    val fakeCardNumber: String = "f" + md5(s"${parent}_${child.id}").take(19)
    card match {
      case number if number.length() > 0 => number
      case _ => fakeCardNumber
    }
  }

  def update = DB.withConnection {
    implicit c =>
      val cardNumber: String = fakeCardIfNeeded
      Logger.info(s"cardNumber = $cardNumber")
      SQL("update relationmap set child_id={child}, " +
        "parent_id={parent}, relationship={relationship}, card_num={card} " +
        "where reference_id={id}")
        .on(
          'parent -> parent.id,
          'child -> child.id,
          'relationship -> relationship,
          'card -> cardNumber,
          'id -> id
        ).executeUpdate()
      None
  }

}

object BatchImportReport {
  implicit val write = Json.writes[BatchImportReport]
  implicit val write1 = Json.writes[SuccessResponse]

  def report(list: List[Option[BatchImportReport]]) = {
    val report = list.filter(_.isDefined).map(_.get)
    report match {
      case x :: xs =>
        Json.toJson(ErrorResponse(report.toString()))
      case List() =>
        Json.toJson(new SuccessResponse)
    }
  }
}
