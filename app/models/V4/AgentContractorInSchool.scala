package models.V4

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class AgentContractorInSchool(id: Option[Long], agent_id: Long, contractor_id: Long, school_id: Long, updated_at: Option[Long]) {
  def hasBeenDeleted(agentId: Long, kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from agentcontractorinschool where agent_id={base} and contractor_id={ad} and school_id={kg} and status=0")
        .on(
          'base -> agentId,
          'ad -> contractor_id,
          'kg -> kg
        ).as(get[Long]("count(1)") single) > 0
  }

  def undoDeletion(agentId: Long, kg: Long) = DB.withConnection {
    implicit c =>
      SQL("update agentcontractorinschool set status=1 where agent_id={base} and contractor_id={ad} and school_id={kg} and status=0")
        .on(
          'base -> agentId,
          'ad -> contractor_id,
          'kg -> kg
        ).executeUpdate()
      id flatMap (AgentContractorInSchool.show(_, agentId, kg))
  }

  def update(base: Long, kg: Long): Option[AgentContractorInSchool] = DB.withConnection {
    implicit c =>
      SQL("update agentcontractorinschool set agent_id={base}, contractor_id={ad}, school_id={kg}, updated_at={time} where uid={id}")
        .on(
          'id -> id,
          'base -> base,
          'ad -> contractor_id,
          'kg -> kg,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (AgentContractorInSchool.show(_, base, kg))
  }

  def create(base: Long, kg: Long): Option[AgentContractorInSchool] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into agentcontractorinschool (agent_id, school_id, contractor_id, updated_at) values (" +
        "{base}, {kg}, {ad}, {time})")
        .on(
          'base -> base,
          'ad -> contractor_id,
          'kg -> kg,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (AgentContractorInSchool.show(_, base, kg))
  }
}
object AgentContractorInSchool {
  implicit val writeAgentContractorInSchool = Json.writes[AgentContractorInSchool]
  implicit val readAgentContractorInSchool = Json.reads[AgentContractorInSchool]

  def show(id: Long, base: Long, kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from agentcontractorinschool where agent_id={base} and school_id={kg} and uid={id} and status=1")
        .on(
          'id -> id,
          'kg -> kg,
          'base -> base
        ).as(simple singleOpt)
  }

  def index(base: Long, kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from agentcontractorinschool where agent_id={base} and school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'from -> from,
          'to -> to,
          'kg -> kg,
          'base -> base
        ).as(simple *)
  }

  def deleteById(id: Long, base: Long, kg: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update agentcontractorinschool set status=0 where agent_id={base} and school_id={kg} and uid={id} and status=1")
        .on(
          'id -> id,
          'kg -> kg,
          'base -> base
        ).executeUpdate()
  }

  def published(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select a.* from agentcontractorinschool s, agentcontractor a where s.contractor_id=a.uid and s.school_id={kg} " +
        s"and a.status=1 and s.status=1 and a.publish_status=2 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'from -> from,
          'to -> to,
          'kg -> kg
        ).as(AgentContractor.simple *)
  }

  val simple = {
    get[Long]("uid") ~
      get[Long]("agent_id") ~
      get[Long]("contractor_id") ~
      get[String]("school_id") ~
      get[Option[Long]]("updated_at") map {
      case id ~ agent ~ ad ~ kg ~ time =>
        AgentContractorInSchool(Some(id), agent, ad, kg.toLong, time)
    }
  }
}
