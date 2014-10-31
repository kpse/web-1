package models

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class Advertisement(id: Option[Long], school_id: Long, position_id: Long, link: String, image: String, name: String, timestamp: Option[Long])

object Advertisement {
  implicit val advertiseWrite = Json.writes[Advertisement]

  def index(schoolId: Long): List[Advertisement] = DB.withConnection {
    implicit c =>
      SQL("select * from advertisement where school_id={kg}")
        .on(
          'kg -> schoolId.toString
        ).as(simple *)
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from advertisement where school_id={kg} and uid = {uid}")
        .on(
          'kg -> kg.toString,
          'uid -> id
        ).as(simple singleOpt)
  }

  val default = Advertisement(Some(0), 0, 0, "", "", "幼乐宝", Some(0))

  val simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[Long]("position_id") ~
      get[String]("link") ~
      get[String]("image") ~
      get[String]("name") ~
      get[Long]("update_at") map {
      case id ~ schoolId ~ position ~ link ~ image ~ name ~ updatedAt =>
        Advertisement(Some(id), schoolId.toLong, position, link, image, name, Some(updatedAt))
    }
  }
}


