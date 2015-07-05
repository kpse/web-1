package models.V4

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class AgentSchool(id: Option[Long], school_id: Long, name: String) {
  def update(base: Long): Option[AgentSchool] = DB.withConnection {
    implicit c =>
      SQL("update agentschool set agent_id={base}, school_id={school_id}, name={name}, " +
        "updated_at={time} where uid={id}")
        .on(
          'id -> id,
          'base -> base,
          'school_id -> school_id,
          'name -> name,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (AgentSchool.show(_, base))
  }

  def create(base: Long): Option[AgentSchool] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into agentschool (agent_id, school_id, name, updated_at) values (" +
        "{base}, {school_id}, {name}, {time})")
        .on(
          'base -> base,
          'school_id -> school_id,
          'name -> name,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (AgentSchool.show(_, base))
  }
}
object AgentSchool {
  implicit val writeAgentSchool = Json.writes[AgentSchool]
  implicit val readAgentSchool = Json.reads[AgentSchool]

  def show(id: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from agentschool where agent_id={base} and uid={id} and status=1")
        .on(
          'id -> id,
          'base -> base
        ).as(simple singleOpt)
  }

  def index(base: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from agentschool where agent_id={base} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'from -> from,
          'to -> to,
          'base -> base
        ).as(simple *)
  }

  def deleteById(id: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update agentschool set status=0 where agent_id={base} and uid={id} and status=1")
        .on(
          'id -> id,
          'base -> base
        ).executeUpdate()
  }

  val simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[String]("name") map {
      case id ~ school ~ name =>
        AgentSchool(Some(id), school.toLong, name)
    }
  }
}
