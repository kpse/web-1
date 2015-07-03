package models.V4

import models.helper.MD5Helper._
import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.Logger
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current
import models.LoginAccount
import play.api.mvc.Session

case class KulebaoAgent(id: Option[Long], name: Option[String], area: Option[String], phone: Option[String], logo: Option[String],
                        login_name: Option[String], updated_at: Option[Long], created_at: Option[Long], expire: Option[Long], privilege_group: Option[String] = Some("agent")) extends LoginAccount {

  override def url(): String = "/agent"

  override def session(): Session = Session(Map("username" -> login_name.getOrElse(""), "phone" -> phone.getOrElse(""), "name" -> name.getOrElse(""), "id" -> id.getOrElse(0).toString))

  def update: Option[KulebaoAgent] = DB.withConnection {
    implicit c =>
      SQL("update agentinfo set name={name}, phone={phone}, area={area}, logo_url={logo_url}, expire_at={expire_at}," +
        "updated_at={time} where uid={id}")
        .on(
          'id -> id,
          'name -> name,
          'area -> area,
          'phone -> phone,
          'logo_url -> logo,
          'login_password -> md5(phone.drop(3).toString()),
          'login_name -> login_name,
          'expire_at -> expire,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap KulebaoAgent.show
  }

  def create: Option[KulebaoAgent] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into agentinfo (name, area, phone, logo_url, login_password, login_name, updated_at, created_at, expire_at) values (" +
        "{name}, {area}, {phone}, {logo_url}, {login_password}, {login_name}, {time}, {time}, {expire_at})")
        .on(
          'name -> name,
          'area -> area,
          'phone -> phone,
          'logo_url -> logo,
          'login_password -> md5(phone.drop(3).toString()),
          'login_name -> login_name,
          'expire_at -> expire,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap KulebaoAgent.show
  }
}

object KulebaoAgent {
  implicit val writeKulebaoAgent = Json.writes[KulebaoAgent]
  implicit val readKulebaoAgent = Json.reads[KulebaoAgent]

  def show(id: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from agentinfo where uid={id} and status=1")
        .on(
          'id -> id
        ).as(simple singleOpt)
  }

  def index(from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from agentinfo where status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'from -> from,
          'to -> to,
          'most -> most
        ).as(simple *)
  }

  def deleteById(id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update agentinfo set status=0 where uid={id} and status=1")
        .on(
          'id -> id
        ).executeUpdate()
  }

  def authenticate(loginName: String, password: String) = DB.withConnection {
    implicit c =>
      SQL("select * from agentinfo where login_name={login} and login_password={password} and status=1")
        .on(
          'login -> loginName,
          'password -> md5(password)
        ).as(simple singleOpt)
  }

  def isAgent(id: Long, path: String) = DB.withConnection {
    implicit c =>
      val isAgent: Boolean = SQL("select count(1) from agentinfo where uid={id} and status=1")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
      Logger.info(s"request path is $path")
      isAgent && (path.matches(s"^(?:/api/v\\d+)?/agent/$id(/.+)?") || path.matches(s"^/agent#/main/$id(/.+)?")) || path.matches("^/agent")
  }

  val simple = {
    get[Long]("uid") ~
      get[Option[String]]("name") ~
      get[Option[String]]("area") ~
      get[Option[String]]("phone") ~
      get[Option[String]]("logo_url") ~
      get[Option[String]]("login_name") ~
      get[Option[Long]]("expire_at") ~
      get[Option[Long]]("updated_at") ~
      get[Option[Long]]("created_at") map {
      case id ~ name ~ area ~ phone ~ url ~ loginName ~ expire ~ updated ~ created =>
        KulebaoAgent(Some(id), name, area, phone, url, loginName, updated, created, expire)
    }
  }
}
