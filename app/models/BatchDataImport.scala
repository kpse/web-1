package models

import anorm.SqlParser._
import anorm._
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
    Parent(Some(id), school_id, name, phone, portrait, gender, birthday, None, member_status, status, company)
  }

  def importing = exists match {
    case true => update
    case false => create
  }

  def create = {
    val parent = transform
    val create1: Option[Parent] = Parent.create(parent.school_id, parent)
    create1 match {
      case Some(x) =>
        None
      case None =>
        Some(BatchImportReport(parent.parent_id.getOrElse(""), "家长 %s 创建失败。".format(parent.parent_id)))
    }
  }

  def update = {
    Parent.update(transform)
    None
  }
}

case class ImportedChild(id: String, name: String, nick: String, birthday: String,
                         gender: Int, portrait: Option[String], class_id: Int,
                         timestamp: Option[Long], school_id: Option[Long], address: Option[String] = None, status: Option[Int] = Some(1)) {
  def transform = {
    ChildInfo(Some(id), name, nick, birthday, gender, portrait, class_id, None, None, school_id, address, status)
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
        Some(BatchImportReport(child.child_id.getOrElse(""), "小孩 %s 创建失败。".format(child.child_id)))
    }
  }

  def update = {
    Children.update(transform)
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
      try {
        SQL("insert into relationmap (child_id, parent_id, card_num, relationship, reference_id) VALUES" +
          " ({child}, {parent}, {card}, {relationship}, {id})")
          .on(
            'parent -> parent.id,
            'child -> child.id,
            'relationship -> relationship,
            'card -> card,
            'id -> id
          ).executeInsert()
        c.commit()
        None
      }
      catch {
        case e: Throwable =>
          Logger.info(e.getLocalizedMessage)
          c.rollback()
          Some(BatchImportReport(id, "卡号 %s（%s <-> %s）创建失败。".format(card, parent.id, child.id)))
      }
  }

  def update = DB.withConnection {
    implicit c =>
      SQL("update relationmap set child_id={child}, " +
        "parent_id={parent}, relationship={relationship}, card_num={card} " +
        "where reference_id={id}")
        .on(
          'parent -> parent.id,
          'child -> child.id,
          'relationship -> relationship,
          'card -> card,
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
        Json.toJson(report)
      case List() =>
        Json.toJson(new SuccessResponse)
    }
  }
}
