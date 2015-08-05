package models.json_models

import anorm.SqlParser._
import anorm.{~, _}
import models._
import org.joda.time.DateTime
import play.Logger
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class SchoolIntro(school_id: Long, phone: String, timestamp: Long, desc: String, school_logo_url: String, name: String, token: Option[String], address: Option[String], full_name: Option[String], properties: Option[List[ConfigItem]] = None, created_at: Option[Long] = None)

case class CreatingSchool(school_id: Long, phone: String, name: String, token: String, principal: PrincipalOfSchool, charge: ChargeInfo, address: String, full_name: Option[String])

case class PrincipalOfSchool(admin_login: String, admin_password: String)

case class SchoolIntroDetail(error_code: Option[Int], school_id: Long, school_info: Option[SchoolIntro])

case class SchoolIntroPreviewResponse(error_code: Int, timestamp: Long, school_id: Long)


object SchoolIntro {
  implicit val configItemWriter = Json.writes[ConfigItem]
  implicit val configItemReader = Json.reads[ConfigItem]
  implicit val schoolIntroWrites = Json.writes[SchoolIntro]
  implicit val schoolIntroRead1 = Json.reads[SchoolIntro]
  implicit val principalOfSchoolRead = Json.reads[PrincipalOfSchool]
  implicit val principalOfSchoolWrite = Json.writes[PrincipalOfSchool]
  implicit val chargeInfoRead = Json.reads[ChargeInfo]
  implicit val chargeInfoWrite = Json.writes[ChargeInfo]
  implicit val creatingSchoolRead = Json.reads[CreatingSchool]
  implicit val creatingSchoolWrite = Json.writes[CreatingSchool]

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
        SQL("insert into schoolinfo (school_id, province, city, address, name, description, logo_url, phone, update_at, token, full_name, created_at) " +
          " values ({school_id}, '', '', {address}, {name}, '', '', {phone}, {timestamp}, {token}, {full_name}, {created})")
          .on(
            'school_id -> school.school_id.toString,
            'timestamp -> time,
            'created -> time,
            'name -> school.name,
            'address -> school.address,
            'phone -> school.phone,
            'token -> school.token,
            'full_name -> school.full_name
          ).executeInsert()
        val employee = Employee(None, "%s校长".format(school.name), school.phone, 0,
          "", "", None, "1980-01-01", school.school_id, school.principal.admin_login, None, None, Some(1))
        val createdPrincipal = school.phone match {
          case (reCreation) if Employee.hasBeenDeleted(reCreation) =>
            Employee.reCreateByPhone(employee)
          case _ =>
            Employee.create(employee)
        }
        createdPrincipal map {
          case (created) =>
            created.promote
            created.updatePassword(school.principal.admin_password)
        }

        SQL("insert into chargeinfo (school_id, total_phone_number, expire_date, update_at) " +
          " values ({school_id}, {total}, {expire}, {time})")
          .on(
            'school_id -> school.school_id.toString,
            'total -> school.charge.total_phone_number,
            'expire -> DateTime.parse(school.charge.expire_date).toString("yyyy-MM-dd"),
            'time -> time
          ).executeInsert()
        SQL("insert into classinfo (school_id, class_id, class_name) " +
          " values ({school_id}, 321, '默认班级')")
          .on(
            'school_id -> school.school_id.toString
          ).executeInsert()
        c.commit()
      }
      catch {
        case t: Throwable =>
          Logger.info("error %s".format(t.toString))
          c.rollback()
          throw new IllegalArgumentException(s"创建学校失败。\n${t.getMessage}", t)
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
    fullName.fold("")(f => " ,full_name={full_name} ")

  }

  def updateExists(info: SchoolIntro) = DB.withTransaction {
    implicit c =>
      val timestamp = System.currentTimeMillis
      Logger.info(info.toString)

      try {
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
        info.properties map {_.map(Charge.addConfig(info.school_id, _))}
      }
      catch {
        case t: Throwable =>
          Logger.info("error %s".format(t.toString))
          c.rollback()
      }

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
      get[Long]("created_at") ~
      get[Long]("update_at") ~
      get[String]("description") ~
      get[String]("logo_url") ~
      get[String]("name") ~
      get[String]("full_name") ~
      get[Option[String]]("token") ~
      get[Option[String]]("address") map {
      case id ~ phone ~ created ~ timestamp ~ desc ~ logoUrl ~ name ~ fullName ~ token ~ address =>
        val config: SchoolConfig = School.config(id.toLong)
        SchoolIntro(id.toLong, phone, timestamp, desc, logoUrl, name, token, address, Some(fullName), Some(config.config), Some(created))
    }

  }


  def detail(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from schoolinfo where school_id={school_id}")
        .on('school_id -> kg.toString).as(sample singleOpt)
  }
}