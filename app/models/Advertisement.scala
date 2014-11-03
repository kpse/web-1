package models

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class Advertisement(id: Option[Long], school_id: Long, position_id: Long, link: String, image: String, name: String, timestamp: Option[Long])

object Advertisement {
  implicit val advertiseWrite = Json.writes[Advertisement]
  implicit val advertiseRead = Json.reads[Advertisement]

  def index(schoolId: Long): List[Advertisement] = DB.withConnection {
    implicit c =>
      SQL("select * from advertisement where school_id={kg} and status=1")
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

  def create(kg: Long, ad: Advertisement) =  DB.withConnection {
    implicit c =>
      SQL("insert into advertisement (school_id, position_id, link, image, name, update_at) " +
        "values ({kg}, {position}, {link}, {image}, {name}, {update_at})")
        .on(
          'kg -> kg.toString,
          'position -> ad.position_id,
          'link -> ad.link,
          'image -> ad.image,
          'name -> ad.name,
          'update_at -> System.currentTimeMillis()
        ).executeInsert()
  }

  def update(kg: Long, ad: Advertisement) =  DB.withConnection {
    implicit c =>
      SQL("update advertisement set school_id={kg}, position_id={position}, link={link}, image={image}, name={name}, update_at={update_at} where uid={id}")
        .on(
          'kg -> kg.toString,
          'position -> ad.position_id,
          'link -> ad.link,
          'image -> ad.image,
          'name -> ad.name,
          'update_at -> System.currentTimeMillis(),
          'id -> ad.id.getOrElse(0)
        ).executeUpdate()
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


