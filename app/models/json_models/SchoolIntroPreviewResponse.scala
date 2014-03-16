package models.json_models

import play.api.db.DB
import anorm._
import play.api.Play.current
import models.helper.FieldHelper._
import play.Logger
import anorm.~
import scala.Some
import anorm.SqlParser._

case class SchoolIntro(school_id: Long, phone: String, timestamp: Long, desc: String, school_logo_url: String, name: String)

case class SchoolIntroDetail(error_code: Option[Int], school_id: Long, school_info: Option[SchoolIntro])

case class SchoolIntroPreviewResponse(error_code: Int, timestamp: Long, school_id: Long)


object SchoolIntro {
  def create(schoolIntro: SchoolIntro) = {
    val time = System.currentTimeMillis
    SQL("insert into schoolinfo (school_id, province, city, name, description, logo_url, phone, update_at) " +
      " values ({school_id}, '四川省', '成都', {name}, {desc}, {url}, {phone}, {timestamp})")
      .on(
        'school_id -> schoolIntro.school_id.toString,
        'timestamp -> time,
        'name -> schoolIntro.name,
        'desc -> schoolIntro.desc,
        'url -> schoolIntro.school_logo_url,
        'phone -> schoolIntro.phone
      )
  }


  def defaultSchoolIntro(name: String, schoolId: Long, time: Long): SchoolIntro = {
    new SchoolIntro(schoolId, "13991855476", time, "描述", "http://www.jslfgz.com.cn/UploadFiles/xxgl/2013/4/201342395834.jpg", name)
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

  def updateExists(info: SchoolIntro) = DB.withConnection {
    implicit c =>
      val timestamp = System.currentTimeMillis
      Logger.info(info.toString)

      SQL("update schoolinfo set name={name}, " +
        "description={description}, phone={phone}, " +
        "logo_url={logo_url}, update_at={timestamp} where school_id={id}")
        .on('id -> info.school_id.toString,
          'name -> info.name,
          'description -> info.desc,
          'phone -> info.phone,
          'logo_url -> info.school_logo_url,
          'timestamp -> timestamp).executeUpdate()

  }

  def preview(kg: Long) = DB.withConnection {
    implicit c =>
      val result = SQL("select update_at, school_id from schoolinfo where school_id={school_id}")
        .on('school_id -> kg.toString).apply()

      if (result.isEmpty) new SchoolIntroPreviewResponse(1, 0, 0)
      else new SchoolIntroPreviewResponse(0, timestamp(result.head), schoolId(result.head))
  }


  def updateOrCreate(info: SchoolIntro) = DB.withConnection {
    implicit c =>
      val exists = schoolExists(info.school_id)
      Logger.info(exists.toString)
      exists match {
        case false =>
          create(info).executeInsert()
        case _ =>
          updateExists(info)
      }
      detail(info.school_id)
  }

  val sample = {
    get[String]("school_id") ~
      get[String]("phone") ~
      get[Long]("update_at") ~
      get[String]("description") ~
      get[String]("logo_url") ~
      get[String]("name") map {
      case id ~ phone ~ timestamp ~ desc ~ logoUrl ~ name =>
        SchoolIntro(id.toLong, phone, timestamp, desc, logoUrl, name)
    }

  }


  def detail(kg: Long) = DB.withConnection {
    implicit c =>
      val result = SQL("select * from schoolinfo where school_id={school_id}")
        .on('school_id -> kg.toString).apply()

      if (result.isEmpty) new SchoolIntroDetail(Some(1), 0, None)
      else {
        val row = result.head
        new SchoolIntroDetail(Some(0), schoolId(row), Some(new SchoolIntro(schoolId(row), phone(row), timestamp(row), desc(row), logoUrl(row), name(row))))
      }
  }
}