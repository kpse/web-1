package models

import java.util.Date

import anorm.SqlParser._
import anorm.{~, _}
import models.helper.MD5Helper.md5
import models.helper.PasswordHelper
import models.helper.TimeHelper.any2DateTime
import play.Logger
import play.api.Play.current
import play.api.db.DB

case class Employee(id: Option[String], name: String, phone: String, gender: Int,
                    workgroup: String, workduty: String, portrait: Option[String],
                    birthday: String, school_id: Long,
                    login_name: String, timestamp: Option[Long], privilege_group: Option[String], status: Option[Int] = Some(1)) {
  def update = DB.withConnection {
    implicit c =>
      id map {
        employeeId =>
          SQL("update employeeinfo set name={name}, phone={phone}, gender={gender}, workgroup={workgroup}, " +
            " workduty={workduty}, picurl={picurl}, birthday={birthday}, school_id={school_id}, " +
            " login_name={login_name}, update_at={update_at}, status={status} " +
            " where employee_id={employee_id}")
            .on(
              'employee_id -> employeeId,
              'name -> name,
              'phone -> phone,
              'gender -> gender,
              'workgroup -> workgroup,
              'workduty -> workduty,
              'picurl -> portrait,
              'birthday -> birthday,
              'school_id -> school_id,
              'login_name -> login_name,
              'status -> status.getOrElse(1),
              'update_at -> System.currentTimeMillis
            ).executeUpdate()
      }
      None
  }

  def create = DB.withTransaction {
    implicit c =>
      val employeeId = id.getOrElse("3_%d_%d".format(school_id, System.currentTimeMillis))
      try {
        val inserted: Option[Long] = SQL("insert into employeeinfo (name, phone, gender, workgroup, workduty, picurl, birthday, school_id, login_name, login_password, update_at, employee_id) " +
          "values ({name},{phone},{gender},{workgroup},{workduty},{portrait},{birthday},{school_id},{login_name},{login_password},{update_at}, {employee_id})")
          .on(
            'employee_id -> employeeId,
            'name -> name,
            'phone -> phone,
            'gender -> gender,
            'workgroup -> workgroup,
            'workduty -> workduty,
            'portrait -> portrait,
            'birthday -> birthday,
            'school_id -> school_id,
            'login_name -> login_name,
            'login_password -> md5(phone),
            'update_at -> System.currentTimeMillis
          ).executeInsert()
        c.commit()
        inserted.flatMap {
          e =>
            Logger.info("finding employee %d".format(e))
            Employee.findById(school_id, employeeId)
        }
      }
      catch {
        case e: Throwable =>
          Logger.info(e.getLocalizedMessage)
          c.rollback()
          None
      }

  }

  def exists = DB.withConnection {
    implicit c =>
      id.exists(employeeId => SQL("select count(1) from employeeinfo where employee_id={id}").on('id -> employeeId).as(get[Long]("count(1)") single) > 0)
  }

  def importing = exists match {
    case true => update
    case false => create match {
      case Some(x) => None
      case None => Some(BatchImportReport(id.getOrElse("unknown"), "老师 %s 创建失败。".format(id.getOrElse("unknown"))))
    }
  }

  def hasHistory(historyId: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from sessionlog where uid={id} and sender={sender}")
        .on(
          'id -> historyId,
          'sender -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def updatePassword(password: String) = DB.withConnection {
    implicit c =>
      SQL("update employeeinfo set login_password={password} where employee_id={id}")
        .on(
          'id -> id,
          'password -> md5(password)
        ).executeUpdate()
  }

  def promote = DB.withConnection {
    implicit c =>
      SQL("insert into privilege (school_id, employee_id, `group`, subordinate, promoter, update_at) " +
        "values ({school_id},{employee_id},{group},{subordinate},{promoter},{time})")
        .on(
          'school_id -> school_id,
          'employee_id -> id,
          'group -> "principal",
          'subordinate -> "",
          'promoter -> "operator",
          'time -> System.currentTimeMillis
        ).executeInsert()
  }
}

case class Principal(employee_id: String, school_id: Long, phone: String, timestamp: Long)

case class EmployeePassword(employee_id: String, school_id: Long, phone: String, old_password: String, login_name: String, new_password: String)

case class EmployeeResetPassword(id: String, school_id: Long, phone: String, login_name: String, new_password: String)

object Employee {

  def permanentRemove(phone: String) = DB.withConnection {
    implicit c =>
      SQL("delete from employeeinfo where phone={phone}")
        .on(
          'phone -> phone
        ).execute()
  }

  def reCreateByPhone(employee: Employee) = DB.withConnection {
    implicit c =>
      val time = System.currentTimeMillis
      val newEmployeeId = "3_%d_%d".format(employee.school_id, time)
      SQL("update employeeinfo set employee_id={employee_id}, name={name}, gender={gender}, workgroup={workgroup}, " +
        " workduty={workduty}, picurl={picurl}, birthday={birthday}, school_id={school_id}, " +
        " login_name={login_name}, update_at={update_at}, status={status} " +
        " where phone={phone}")
        .on(
          'employee_id -> newEmployeeId,
          'name -> employee.name,
          'phone -> employee.phone,
          'gender -> employee.gender,
          'workgroup -> employee.workgroup,
          'workduty -> employee.workduty,
          'picurl -> employee.portrait,
          'birthday -> employee.birthday,
          'school_id -> employee.school_id,
          'login_name -> employee.login_name,
          'status -> 1,
          'update_at -> time
        ).executeUpdate()

      show(employee.phone)
  }

  def hasBeenDeleted(phone: String) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from employeeinfo where phone={phone} and status=0")
        .on(
          'phone -> phone
        ).as(get[Long]("count(1)") single) > 0
  }

  def findById(kg: Long, id: String) = DB.withConnection {
    implicit c =>
      SQL("select * from employeeinfo where employee_id={id} and status=1 and school_id in ({kg}, '0')")
        .on(
          'id -> id,
          'kg -> kg.toString
        ).as(simple singleOpt)
  }

  def getPhoneByLoginName(loginName: String): String = DB.withConnection {
    implicit c =>
      SQL("select phone from employeeinfo where login_name={name} and status=1")
        .on(
          'name -> loginName
        ).as(get[String]("phone") singleOpt).getOrElse("")
  }

  def matchPasswordRule(password: EmployeePassword) = PasswordHelper.isValid(password.new_password)

  def findByName(kg: Long, name: String) = DB.withConnection {
    implicit c =>
      SQL("select * from employeeinfo where name={name} and school_id={kg} and status=1")
        .on(
          'name -> name,
          'kg -> kg.toString
        ).as(simple singleOpt)
  }

  def managedClass(kg: Long, employee: Employee) = DB.withConnection {
    implicit c =>
      val classes: List[String] = SQL("select subordinate from privilege where employee_id={id} and school_id={kg}")
        .on(
          'id -> employee.id,
          'kg -> kg
        ).as(get[String]("subordinate") *)
      Logger.info(classes.toString())
      classes map {
        c =>
          School.findClass(kg, Some(Integer.parseInt(c)))
      } filter {
        case Some(SchoolClass(_, _, _, _, status)) => status.getOrElse(0) == 1
      }
  }

  def isSuperUser(id: String, kg: Long) = isPrincipal(id, kg) || isOperator(id)

  def phoneChanged(employee: Employee): Boolean = DB.withConnection {
    implicit c =>
      SQL("select count(1) from employeeinfo where employee_id={id} and school_id={kg} and phone={phone}")
        .on(
          'id -> employee.id,
          'phone -> employee.phone,
          'kg -> employee.school_id.toString
        ).as(get[Long]("count(1)") single) == 0
  }

  def oldPasswordMatch(password: EmployeePassword) = {
    authenticate(password.login_name, password.old_password).nonEmpty
  }

  def resetPassword(password: EmployeeResetPassword) = DB.withConnection {
    implicit c =>
      SQL("update employeeinfo set login_password={new_password} where " +
        "phone={phone} and employee_id={id} and login_name={login_name}")
        .on(
          'id -> password.id,
          'phone -> password.phone,
          'new_password -> md5(password.new_password),
          'login_name -> password.login_name,
          'update_at -> System.currentTimeMillis
        ).executeUpdate()
  }

  def idExists(employeeId: Option[String]): Boolean = DB.withConnection {
    implicit c =>
      employeeId match {
        case Some(id) =>
          SQL("select count(1) from employeeinfo where employee_id={employee_id}")
            .on('employee_id -> id)
            .as(get[Long]("count(1)") single) > 0
        case None => false
      }
  }


  def loginNameExists(loginName: String) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from employeeinfo where login_name={login}")
        .on(
          'login -> loginName
        ).as(get[Long]("count(1)") single) > 0
  }

  def phoneExists(phone: String) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from employeeinfo where phone={phone} and status=1")
        .on(
          'phone -> phone
        ).as(get[Long]("count(1)") single) > 0
  }

  def phoneSearch(phone: String) = DB.withConnection {
    implicit c =>
      SQL("select * from employeeinfo where phone={phone} and status=1")
        .on(
          'phone -> phone
        ).as(simple singleOpt)
  }


  def isOperator(id: String) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from privilege where `group`='operator' and employee_id={id}")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def isPrincipal(id: String, kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from privilege where `group`='principal' and employee_id={id} and school_id={kg}")
        .on(
          'id -> id,
          'kg -> kg
        ).as(get[Long]("count(1)") single) > 0
  }


  def isTeacher(id: String, kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from employeeinfo where employee_id={id} and school_id={kg} and status=1")
        .on(
          'id -> id,
          'kg -> kg.toString
        ).as(get[Long]("count(1)") single) > 0
  }

  def canAccess(id: Option[String], schoolId: Long = 0): Boolean = id.exists {
    case (userId) if isOperator(userId) => true
    case (userId) if isPrincipal(userId, schoolId) => true
    case (userId) if isTeacher(userId, schoolId) => true
    case _ => false
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

  def createPrincipal(employee: Employee) = {
    create(employee) map(_.promote)
  }

  def update(employee: Employee) = DB.withConnection {
    implicit c =>
      employee.update
      show(employee.phone)
  }

  def deleteInSchool(kg: Long, phone: String): Option[Employee] = DB.withTransaction {
    implicit c =>
      val result: Option[Employee] = phoneSearch(phone)
      try {
        result map {
          employee =>
            SQL("update employeeinfo set status=0 where employee_id={id} and school_id={kg}")
              .on(
                'kg -> kg.toString,
                'id -> employee.id
              ).executeUpdate()
            SQL("update privilege set status=0 where school_id={kg} and employee_id={id}")
              .on('kg -> kg, 'id -> employee.id).executeUpdate()
        }
        c.commit()
        result
      }
      catch {
        case e: Throwable =>
          c.rollback()
          return None
      }
  }

  def generatePhoneQuery(phones: Option[String]): String = {
    phones match {
      case Some(phone) =>
        " and phone in (" + phone + ") "
      case None =>
        " "
    }
  }

  def allInSchool(kg: Long, phone: Option[String]) = DB.withConnection {
    implicit c =>
      SQL("select * from employeeinfo where status=1 and school_id={kg} " + generatePhoneQuery(phone))
        .on(
          'kg -> kg.toString,
          'phone -> phone
        ).as(simple *)
  }

  def create(employee: Employee) = DB.withConnection {
    implicit c =>
      employee.create
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

  def groupOf(employeeId: String, schoolId: Long): Option[String] = employeeId match {
    case (id) if isOperator(id) => Some("operator")
    case (id) if isPrincipal(id, schoolId) => Some("principal")
    case _ => Some("teacher")
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
        Employee(Some(id), name, phone, gender, workgroup, workduty, Some(url.getOrElse("")), birthday.toDateOnly, kg.toLong, loginName, Some(timestamp), groupOf(id, kg.toLong))
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

object Teacher {
  def unapply(loginName: String) = DB.withConnection {
    implicit c =>
      SQL("select * from employeeinfo where login_name={login_name}")
        .on(
          'login_name -> loginName
        ).as(Employee.simple singleOpt)
  }
}
