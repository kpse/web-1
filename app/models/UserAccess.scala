package models

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import anorm.~
import play.api.Play.current
import play.Logger

case class UserAccess(school_id: Long, id: String, group: String, subordinate: String)

object UserAccess {
  def allClasses(accesses: List[UserAccess]) = accesses.map(_.subordinate).mkString(",")

  val simple = {
    get[String]("school_id") ~
      get[String]("employee_id") ~
      get[String]("group") ~
      get[String]("subordinate") map {
      case kg ~ id ~ group ~ subordinate =>
        UserAccess(kg.toLong, id, group, subordinate)
    }
  }

  def queryByUsername(name: String, kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select p.* from employeeinfo e, privilege p " +
        "where e.employee_id=p.employee_id and e.login_name={name} " +
        "and e.school_id in ({kg}, '0') and e.status=1")
        .on('name -> name, 'kg -> kg.toString).as(simple *)
  }

  def isSupervisor(accesses: List[UserAccess]): Boolean = {
    accesses.exists((u: UserAccess) => List("principal", "operator").contains(u.group))
  }

  def filter(access: List[UserAccess])(result: List[SchoolClass]): List[SchoolClass] = {
    result.partition(c => access.exists(a => List("principal", "operator").contains(a.group) || a.subordinate.equals(c.class_id.getOrElse(0).toString)))._1
  }

  def filterClassId(access: List[UserAccess])(classId: Option[Long]): Option[Long] = {
    isSupervisor(access) match {
      case true =>
        classId
      case false =>
        access.find((a: UserAccess) => a.subordinate.equals(classId.getOrElse(-1).toString)) match {
          case Some(x) => classId
          case None => None
        }
    }

  }
}
