package models

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import anorm.~
import play.api.Play.current
import java.util.Date
import models.helper.TimeHelper.any2DateTime
import models.helper.MD5Helper.md5

case class Employee(id: String, name: String, phone: String, gender: Int, workgroup: String, workduty: String,
                    portrait: String, birthday: String, school_id: Long, login_name: String, login_password: String, timestamp: Long)


object Employee {

  def authenticate(loginName: String, password: String) = DB.withConnection {
    implicit c =>
      SQL("select * from employeeinfo where login_name={login} and login_password={password}")
        .on(
          'login -> loginName,
          'password -> md5(password)
        ).as(simple singleOpt)
  }

  def show(kg: Long, phone: String) = DB.withConnection {
    implicit c =>
      SQL("select * from employeeinfo where school_id={kg} and phone={phone}")
        .on(
          'kg -> kg.toString,
          'phone -> phone
        ).as(simple singleOpt)
  }

  val simple = {
    get[String]("employee_id") ~
      get[String]("name") ~
      get[String]("phone") ~
      get[Int]("gender") ~
      get[String]("workgroup") ~
      get[String]("workduty") ~
      get[String]("picurl") ~
      get[Date]("birthday") ~
      get[String]("school_id") ~
      get[String]("login_name") ~
      get[String]("login_password") ~
      get[Long]("update_at") map {
      case id ~ name ~ phone ~ gender ~ workgroup ~ workduty ~ url ~ birthday ~ kg ~ loginName ~ loginPassword ~ timestamp =>
        Employee(id, name, phone, gender, workgroup, workduty, url, birthday.toDateOnly, kg.toLong, loginName, loginPassword, timestamp)
    }
  }

  def all(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from employeeinfo where school_id={kg}")
        .on(
          'kg -> kg.toString
        ).as(simple *)
  }

}
