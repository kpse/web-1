package models.V4

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class AgentReport(threshold: Long, current: Long)

case class AgentSummaryInSchool(agent_id: Long, contractor: AgentReport, activity: AgentReport, school_id: Long)

case class AgentSchool(id: Option[Long], school_id: Long, name: String, address: Option[String] = None, created_at: Option[Long] = None) {
  def update(base: Long): Option[AgentSchool] = DB.withConnection {
    implicit c =>
      SQL("update agentschool set agent_id={base}, school_id={school_id}, name={name}, address={address}, " +
        "updated_at={time} where uid={id}")
        .on(
          'id -> id,
          'base -> base,
          'school_id -> school_id,
          'name -> name,
          'address -> address,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (AgentSchool.show(_, base))
  }

  def create(base: Long): Option[AgentSchool] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into agentschool (agent_id, school_id, name, address, updated_at, created_at) values (" +
        "{base}, {school_id}, {name}, {address}, {time}, {time})")
        .on(
          'base -> base,
          'school_id -> school_id,
          'name -> name,
          'address -> address,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (AgentSchool.show(_, base))
  }

  def connected = DB.withTransaction {
    implicit c =>
      SQL("select count(1) from agentschool where school_id={kg} and status=1")
        .on(
          'kg -> school_id.toString
        ).as(get[Long]("count(1)") single) > 0
  }
}

object AgentSchool {
  implicit val writeAgentSchool = Json.writes[AgentSchool]
  implicit val readAgentSchool = Json.reads[AgentSchool]
  implicit val writeAgentReport = Json.writes[AgentReport]
  implicit val readAgentReport = Json.reads[AgentReport]
  implicit val writeAgentContractorInSchoolSummary = Json.writes[AgentSummaryInSchool]
  implicit val readAgentContractorInSchoolSummary = Json.reads[AgentSummaryInSchool]

  def show(id: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from agentschool where agent_id={base} and uid={id} and status=1")
        .on(
          'id -> id,
          'base -> base
        ).as(simple singleOpt)
  }

  def exists(agentId: Long, kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from agentschool where agent_id={base} and school_id={kg} and status=1")
        .on(
          'base -> agentId,
          'kg -> kg.toString
        ).as(get[Long]("count(1)") single) > 0
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

  def hasAgent(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from agentschool where school_id={kg} and status=1")
        .on(
          'kg -> kg.toString
        ).as(get[Long]("count(1)") single) > 0
  }

  def summarise(kg: Long): AgentSummaryInSchool = DB.withConnection {
    implicit c =>
      SQL(s"select (select count(a.uid) from agentcontractorinschool s, agentcontractor a where s.contractor_id=a.uid and s.school_id={kg} and a.status=1 and s.status=1 and a.publish_status=4) as contractor, " +
        s"(select count(a.uid) from agentactivityinschool s, agentactivity a where s.activity_id=a.uid and s.school_id={kg} and a.status=1 and s.status=1 and a.publish_status=4) as activity, " +
        s"{kg} as school_id, " +
        s"(select agent_id from agentschool where school_id={kg} and status=1 limit 1) as agent")
        .on(
          'kg -> kg.toString
        ).as(simpleSummary single)
  }

  val simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[Option[String]]("address") ~
      get[Option[Long]]("created_at") ~
      get[String]("name") map {
      case id ~ school ~ address ~ time ~ name =>
        AgentSchool(Some(id), school.toLong, name, address, time)
    }
  }

  val simpleSummary = {
    get[Long]("agent") ~
      get[String]("school_id") ~
      get[Long]("contractor") ~
      get[Long]("activity") map {
      case agent ~ kg ~ contractor ~ activity =>
        AgentSummaryInSchool(agent, AgentReport(5, contractor), AgentReport(5, activity), kg.toLong)
    }
  }
}
