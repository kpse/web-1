package models.V4

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class AgentActivityInSchool(id: Option[Long], agent_id: Long, activity_id: Long, school_id: Long, updated_at: Option[Long]) {
  def hasBeenDeleted(agentId: Long, kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from agentactivityinschool where agent_id={base} and activity_id={ad} and school_id={kg} and status=0")
        .on(
          'base -> agentId,
          'ad -> activity_id,
          'kg -> kg
        ).as(get[Long]("count(1)") single) > 0
  }

  def undoDeletion(agentId: Long, kg: Long) = DB.withConnection {
    implicit c =>
      SQL("update agentactivityinschool set status=1 where agent_id={base} and activity_id={ad} and school_id={kg} and status=0")
        .on(
          'base -> agentId,
          'ad -> activity_id,
          'kg -> kg
        ).executeUpdate()
      id flatMap (AgentContractorInSchool.show(_, agentId, kg))
  }

  def update(base: Long, kg: Long): Option[AgentActivityInSchool] = DB.withConnection {
    implicit c =>
      SQL("update agentactivityinschool set agent_id={base}, activity_id={ad}, school_id={kg}, updated_at={time} where uid={id}")
        .on(
          'id -> id,
          'base -> base,
          'ad -> activity_id,
          'kg -> kg,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (AgentActivityInSchool.show(_, base, kg))
  }

  def create(base: Long, kg: Long): Option[AgentActivityInSchool] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into agentactivityinschool (agent_id, school_id, activity_id, updated_at) values (" +
        "{base}, {kg}, {ad}, {time})")
        .on(
          'base -> base,
          'ad -> activity_id,
          'kg -> kg,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (AgentActivityInSchool.show(_, base, kg))
  }
}

object AgentActivityInSchool {
  implicit val writeAgentActivityInSchool = Json.writes[AgentActivityInSchool]
  implicit val readAgentActivityInSchool = Json.reads[AgentActivityInSchool]

  def show(id: Long, base: Long, kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from agentactivityinschool where agent_id={base} and school_id={kg} and uid={id} and status=1")
        .on(
          'id -> id,
          'kg -> kg,
          'base -> base
        ).as(simple singleOpt)
  }

  def index(base: Long, kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from agentactivityinschool where agent_id={base} and school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'from -> from,
          'to -> to,
          'kg -> kg,
          'base -> base
        ).as(simple *)
  }

  def deleteById(id: Long, base: Long, kg: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update agentactivityinschool set status=0 where agent_id={base} and school_id={kg} and uid={id} and status=1")
        .on(
          'id -> id,
          'kg -> kg,
          'base -> base
        ).executeUpdate()
  }

  def published(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select a.* from agentactivityinschool s, agentadvertisement a where s.activity_id=a.uid and s.school_id={kg} " +
        s"and a.status=1 and s.status=1 and a.publish_status=2 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'from -> from,
          'to -> to,
          'kg -> kg
        ).as(AgentActivity.simple *)
  }

  val simple = {
    get[Long]("uid") ~
      get[Long]("agent_id") ~
      get[Long]("activity_id") ~
      get[String]("school_id") ~
      get[Option[Long]]("updated_at") map {
      case id ~ agent ~ ad ~ kg ~ time =>
        AgentActivityInSchool(Some(id), agent, ad, kg.toLong, time)
    }
  }
}
