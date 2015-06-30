package models.V4

import models.helper.MD5Helper._
import anorm.SqlParser._
import anorm._
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current
import models.LoginAccount
import play.api.mvc.Session

case class KulebaoAgent(id: Option[Long], name: Option[String], phone: Option[String], logo: Option[String],
                        login_name: Option[String], updated_at: Option[Long], created_at: Option[Long], privilege_group: Option[String] = Some("agent")) extends LoginAccount {

  override def url(): String = "/agent"

  override def session(): Session = Session(Map("username" -> login_name.getOrElse(""), "phone" -> phone.getOrElse(""), "name" -> name.getOrElse(""), "id" -> id.getOrElse(0).toString))
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

  def authenticate(loginName: String, password: String) = DB.withConnection {
    implicit c =>
      SQL("select * from agentinfo where login_name={login} and login_password={password} and status=1")
        .on(
          'login -> loginName,
          'password -> md5(password)
        ).as(simple singleOpt)
  }

  def isAgent(id: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from agentinfo where uid={id} and status=1")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  val simple = {
    get[Long]("uid") ~
      get[Option[String]]("name") ~
      get[Option[String]]("phone") ~
      get[Option[String]]("logo_url") ~
      get[Option[String]]("login_name") ~
      get[Option[Long]]("update_at") ~
      get[Option[Long]]("created_at") map {
      case id ~ name ~ phone ~ url ~ loginName ~ updated ~ created =>
        KulebaoAgent(Some(id), name, phone, url, loginName, updated, created)
    }
  }
}
