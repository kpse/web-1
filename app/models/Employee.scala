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
  def deleteInSchool(kg: Long, phone: String) = DB.withConnection {
    implicit c =>
      SQL("update employeeinfo set status=0 where phone={phone} and school_id={kg}")
        .on(
          'kg -> kg.toString,
          'phone -> phone
        ).execute()
  }

  def allInSchool(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from employeeinfo where status=1 and school_id={kg}")
        .on(
          'kg -> kg.toString
        ).as(simple *)
  }

  def create(employee: Employee) = DB.withConnection {
    implicit c =>
      val employeeId = "3_%d".format(System.currentTimeMillis)
      SQL("insert into employeeinfo (name, phone, gender, workgroup, workduty, picurl, birthday, school_id, login_name, login_password, update_at, employee_id) " +
        "values ({name},{phone},{gender},{workgroup},{workduty},{portrait},{birthday},{school_id},{login_name},{login_password},{update_at}, {employee_id})")
        .on(
          'employee_id -> employeeId,
          'name -> employee.name,
          'phone -> employee.phone,
          'gender -> employee.gender,
          'workgroup -> employee.workgroup,
          'workduty -> employee.workduty,
          'portrait -> employee.portrait,
          'birthday -> employee.birthday,
          'school_id -> employee.school_id,
          'login_name -> employee.login_name,
          'login_password -> md5(employee.login_name),
          'update_at -> System.currentTimeMillis
        ).executeInsert()
      show(employee.phone)
  }


  def authenticate(loginName: String, password: String) = DB.withConnection {
    implicit c =>
      SQL("select * from employeeinfo where login_name={login} and login_password={password} and status=1")
        .on(
          'login -> loginName,
          'password -> md5(password)
        ).as(simple singleOpt)
  }

  def show(phone: String) = DB.withConnection {
    implicit c =>
      SQL("select * from employeeinfo where status=1 and phone={phone}")
        .on(
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

  def all = DB.withConnection {
    implicit c =>
      SQL("select * from employeeinfo where status=1").as(simple *)
  }

}
