package models

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import anorm.~
import play.api.Play.current
import java.util.Date
import models.helper.TimeHelper.any2DateTime
import models.helper.MD5Helper.md5
import play.Logger

case class Employee(id: Option[String], name: String, phone: String, gender: Int,
                    workgroup: String, workduty: String, portrait: Option[String],
                    birthday: String, school_id: Long,
                    login_name: String, timestamp: Option[Long])

case class Principal(employee_id: String, school_id: Long, phone: String, timestamp: Long)

case class EmployeePassword(employee_id: String, school_id: Long, phone: String, old_password: String, login_name: String, new_password: String)

object Employee {

  def isOperator(id: String) = DB.withConnection {
    implicit c =>
      Logger.info(id)
      SQL("select count(1) from privilege where `group`='operator' and employee_id={id}")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def isPrincipal(id: String, kg: Long) = DB.withConnection {
    implicit c =>
      Logger.info(id)
      SQL("select count(1) from privilege where `group`='principal' and employee_id={id} and school_id={kg}")
        .on(
          'id -> id,
          'kg -> kg
        ).as(get[Long]("count(1)") single) > 0
  }


  def canAccess(id: Option[String], schoolId: Long = 0): Boolean = id.exists {
    case (userId) if isOperator(userId) => true
    case (userId) if isPrincipal(userId, schoolId) => true
    case _ =>
      Logger.info("employee canAccess false")
      false
  }

  def changPassword(kg: Long, phone: String, password: EmployeePassword) = DB.withConnection {
    implicit c =>
      SQL("update employeeinfo set login_password={new_password} where school_id={kg} and " +
        "phone={phone} and login_name={login_name} and login_password={old_password}")
        .on(
          'phone -> phone,
          'new_password -> md5(password.new_password),
          'old_password -> md5(password.old_password),
          'kg -> kg.toString,
          'login_name -> password.login_name,
          'update_at -> System.currentTimeMillis
        ).executeUpdate()
  }


  def allPrincipal(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select p.*, e.phone from privilege p, employeeinfo e where e.employee_id=p.employee_id " +
        "and e.status=1 and p.status=1 and p.school_id=e.school_id and p.school_id={kg} " +
        "and `group`='principal'")
        .on(
          'kg -> kg.toString
        ).as(simplePrincipal *)
  }


  def promote(employee: Employee) = DB.withConnection {
    implicit c =>
      SQL("insert into privilege (school_id, employee_id, `group`, subordinate, promoter, update_at) " +
        "values ({school_id},{employee_id},{group},{subordinate},{promoter},{time})")
        .on(
          'school_id -> employee.school_id,
          'employee_id -> employee.id,
          'group -> "principal",
          'subordinate -> "",
          'promoter -> "operator",
          'time -> System.currentTimeMillis
        ).executeInsert()
      show(employee.phone)
  }

  def createPrincipal(employee: Employee) = {
    val created: Option[Employee] = create(employee)
    created map {
      case (admin) =>
        promote(admin)
    }
  }

  def update(employee: Employee) = DB.withConnection {
    implicit c =>
      employee.id map {
        employeeId =>
          SQL("update employeeinfo set name={name}, phone={phone}, gender={gender}, workgroup={workgroup}, " +
            " workduty={workduty}, picurl={picurl}, birthday={birthday}, school_id={school_id}, " +
            " login_name={login_name}, update_at={update_at}, employee_id={employee_id} " +
            " where employee_id={employee_id}")
            .on(
              'employee_id -> employeeId,
              'name -> employee.name,
              'phone -> employee.phone,
              'gender -> employee.gender,
              'workgroup -> employee.workgroup,
              'workduty -> employee.workduty,
              'picurl -> employee.portrait,
              'birthday -> employee.birthday,
              'school_id -> employee.school_id,
              'login_name -> employee.login_name,
              'update_at -> System.currentTimeMillis
            ).executeUpdate()
      }

      show(employee.phone)
  }

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
      get[Option[String]]("picurl") ~
      get[Date]("birthday") ~
      get[String]("school_id") ~
      get[String]("login_name") ~
      get[Long]("update_at") map {
      case id ~ name ~ phone ~ gender ~ workgroup ~ workduty ~ url ~ birthday ~ kg ~ loginName ~ timestamp =>
        Employee(Some(id), name, phone, gender, workgroup, workduty, url, birthday.toDateOnly, kg.toLong, loginName, Some(timestamp))
    }
  }

  val simplePrincipal = {
    get[String]("employee_id") ~
      get[String]("phone") ~
      get[String]("school_id") ~
      get[Long]("update_at") map {
      case id ~ phone ~ kg ~ timestamp =>
        Principal(id, kg.toLong, phone, timestamp)
    }
  }

  def all = DB.withConnection {
    implicit c =>
      SQL("select * from employeeinfo where status=1").as(simple *)
  }

}
