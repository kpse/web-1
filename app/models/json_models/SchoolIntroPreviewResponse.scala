package models.json_models

import play.api.db.DB
import anorm._
import play.api.Play.current
import play.Logger
import anorm.~
import scala.Some
import models.helper.MD5Helper.md5
import anorm.SqlParser._
import models.ChargeInfo

case class SchoolIntro(school_id: Long, phone: String, timestamp: Long, desc: String, school_logo_url: String, name: String, token: Option[String], address: Option[String], full_name: Option[String])

case class CreatingSchool(school_id: Long, phone: String, name: String, token: String, principal: PrincipalOfSchool, charge: ChargeInfo, address: String, full_name: Option[String])

case class PrincipalOfSchool(admin_login: String, admin_password: String)

case class SchoolIntroDetail(error_code: Option[Int], school_id: Long, school_info: Option[SchoolIntro])

case class SchoolIntroPreviewResponse(error_code: Int, timestamp: Long, school_id: Long)


object SchoolIntro {
  def adminExists(adminLogin: String) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from employeeinfo where login_name={name}").on('name -> adminLogin).as(get[Long]("count(1)") single) > 0
  }

  def idExists(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from schoolinfo where school_id={id}").on('id -> kg).as(get[Long]("count(1)") single) > 0
  }

  def create(school: CreatingSchool) = DB.withTransaction {
    implicit c =>
      try {
        val time = System.currentTimeMillis
        SQL("insert into schoolinfo (school_id, province, city, address, name, description, logo_url, phone, update_at, token, full_name) " +
          " values ({school_id}, '', '', {address}, {name}, '', '', {phone}, {timestamp}, {token}, {full_name})")
          .on(
            'school_id -> school.school_id.toString,
            'timestamp -> time,
            'name -> school.name,
            'address -> school.address,
            'phone -> school.phone,
            'token -> school.token,
            'full_name -> school.full_name
          ).executeInsert()
        val employeeId = "3_%d_%d".format(school.school_id, time)
        SQL("insert into employeeinfo (name, employee_id, phone, gender, workgroup, workduty, picurl, birthday, school_id, login_password, login_name) " +
          " values ({name}, {employee_id}, {phone}, 0, '', '', '', '1980-01-01', {school_id}, {password}, {login_name})")
          .on(
            'school_id -> school.school_id.toString,
            'name -> "%s校长".format(school.name),
            'employee_id -> employeeId,
            'login_name -> school.principal.admin_login,
            'password -> md5(school.principal.admin_password),
            'phone -> school.phone
          ).executeInsert()
        SQL("insert into privilege (school_id, employee_id, `group`, subordinate, promoter, update_at) " +
          "values ({school_id},{employee_id},{group},{subordinate},{promoter},{time})")
          .on(
            'school_id -> school.school_id.toString,
            'employee_id -> employeeId,
            'group -> "principal",
            'subordinate -> "",
            'promoter -> "operator",
            'time -> time
          ).executeInsert()
        SQL("insert into chargeinfo (school_id, total_phone_number, expire_date, update_at) " +
          " values ({school_id}, {total}, {expire}, {time})")
          .on(
            'school_id -> school.school_id.toString,
            'total -> school.charge.total_phone_number,
            'expire -> school.charge.expire_date,
            'time -> time
          ).executeInsert()
        SQL("insert into classinfo (school_id, class_id, class_name) " +
          " values ({school_id}, 1, '默认班级')")
          .on(
            'school_id -> school.school_id.toString
          ).executeInsert()
        c.commit()
      }
      catch {
        case t: Throwable =>
          Logger.info("error %s".format(t.toString))
          c.rollback()
      }
      detail(school.school_id)
  }

  def index = DB.withConnection {
    implicit c =>
      SQL("select * from schoolinfo").as(sample *)
  }

  def schoolExists(schoolId: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from schoolinfo where school_id={school_id}")
        .on('school_id -> schoolId)
        .as(get[Long]("count(1)") single) > 0
  }

  def generateFullNameSql(fullName: Option[String]): String = {
    fullName.map(f => " ,full_name={full_name} ").getOrElse("")

  }

  def updateExists(info: SchoolIntro) = DB.withConnection {
    implicit c =>
      val timestamp = System.currentTimeMillis
      Logger.info(info.toString)

      SQL("update schoolinfo set name={name}, " +
        "description={description}, phone={phone}, " +
        "logo_url={logo_url}, update_at={timestamp}, token={token}, address={address} " +
        generateFullNameSql(info.full_name) +
        "where school_id={id}")
        .on('id -> info.school_id.toString,
          'name -> info.name,
          'description -> info.desc,
          'phone -> info.phone,
          'logo_url -> info.school_logo_url,
          'token -> info.token,
          'address -> info.address,
          'full_name -> info.full_name,
          'timestamp -> timestamp).executeUpdate()

  }

  val previewSimple = {
    get[String]("school_id") ~
      get[Long]("update_at") map {
      case school_id ~ timestamp =>
        SchoolIntroPreviewResponse(0, timestamp, school_id.toLong)
    }
  }

  def preview(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select update_at, school_id from schoolinfo where school_id={school_id}")
        .on('school_id -> kg.toString).as(previewSimple *)
  }

  val sample = {
    get[String]("school_id") ~
      get[String]("phone") ~
      get[Long]("update_at") ~
      get[String]("description") ~
      get[String]("logo_url") ~
      get[String]("name") ~
      get[String]("full_name") ~
      get[Option[String]]("token") ~
      get[Option[String]]("address") map {
      case id ~ phone ~ timestamp ~ desc ~ logoUrl ~ name ~ fullName ~ token ~ address =>
        SchoolIntro(id.toLong, phone, timestamp, desc, logoUrl, name, token, address, Some(fullName))
    }

  }


  def detail(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from schoolinfo where school_id={school_id}")
        .on('school_id -> kg.toString).as(sample singleOpt)
  }
}