package models

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import play.api.Play.current

case class BatchImportReport(id: String, reason: String)

case class ImportedParent(id: String, school_id: Long, name: String, phone: String, gender: Int, portrait: Option[String], birthday: String, member_status: Option[Int], status: Option[Int], company: Option[String] = None, timestamp: Option[Long]) {
  def exists = DB.withConnection {
    implicit c =>
      SQL("select count(1) from parentinfo where parent_id={id}").on('id -> id).as(get[Long]("count(1)") single) > 0
  }

  def transform = {
    Parent(Some(id), school_id, name, phone, portrait, gender, birthday, None, member_status, status, company)
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

object BatchDataImport {
  def createOrUpdate(parents: List[ImportedParent]): List[Option[BatchImportReport]] = DB.withTransaction {
    implicit c =>
      parents.map {
        case (p: ImportedParent) if p.exists =>
          p.create
        case p =>
          p.update
      }
  }

}
