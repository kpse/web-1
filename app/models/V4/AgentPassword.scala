package models.V4

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current
import models.helper.MD5Helper.md5

case class AgentPassword(agent_id: Long, old_password: Option[String], new_password: Option[String], login_name: String) {
  def matched = DB.withConnection {
    implicit c =>
      SQL("select count(1) from agentinfo where login_name={login_name} and uid={id} and login_password={p}")
        .on(
          'id -> agent_id,
          'login_name -> login_name,
          'p -> old_password.map(md5)
        ).as(get[Long]("count(1)") single) > 0
  }

  def change = DB.withConnection {
    implicit c =>
      SQL("update agentinfo set login_password={newP} where login_name={login_name} and uid={id} and login_password={p}")
        .on(
          'id -> agent_id,
          'login_name -> login_name,
          'newP -> new_password.map(md5),
          'p -> old_password.map(md5)
        ).executeUpdate()
  }

}

case class AgentResetPassword(agent_id: String, authcode: String, login_name: String, password: String) {
  def matched = DB.withConnection {
    implicit c =>
      SQL("select count(1) from agentinfo where login_name={login_name} and uid={id}")
        .on(
          'id -> agent_id,
          'login_name -> login_name
        ).as(get[Long]("count(1)") single) > 0
  }

  def reset = DB.withConnection {
    implicit c =>
      SQL("update agentinfo set login_password={p} where login_name={login_name} and uid={id}")
        .on(
          'id -> agent_id,
          'login_name -> login_name,
          'p -> md5(password)
        ).executeUpdate()
  }
}

object AgentPassword {
  implicit val readAgentPassword = Json.reads[AgentPassword]
  implicit val readAgentResetPassword = Json.reads[AgentResetPassword]
}
