package models.V4

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class AgentSchoolAd(id: Option[Long], agent_id: Long, ad_id: Long, school_id: Long, updated_at: Option[Long], publish_status: Int) {
  def update(base: Long, kg: Long): Option[AgentSchoolAd] = DB.withConnection {
    implicit c =>
      SQL("update agentadinschool set agent_id={base}, ad_id={ad}, school_id={kg}, publish_status={publish}, updated_at={time} where uid={id}")
        .on(
          'id -> id,
          'base -> base,
          'ad -> ad_id,
          'kg -> kg,
          'publish -> publish_status,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (AgentSchoolAd.show(_, base))
  }

  def create(base: Long, kg: Long): Option[AgentSchoolAd] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into agentadinschool (agent_id, school_id, ad_id, publish_status, updated_at) values (" +
        "{base}, {kg}, {ad}, {publish}, {time})")
        .on(
          'base -> base,
          'ad -> ad_id,
          'kg -> kg,
          'publish -> publish_status,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (AgentSchoolAd.show(_, base))
  }
}
object AgentSchoolAd {
  implicit val writeAgentSchoolAd = Json.writes[AgentSchoolAd]
  implicit val readAgentSchoolAd = Json.reads[AgentSchoolAd]

  def show(id: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from agentadinschool where agent_id={base} and uid={id} and status=1")
        .on(
          'id -> id,
          'base -> base
        ).as(simple singleOpt)
  }

  def index(base: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from agentadinschool where agent_id={base} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'from -> from,
          'to -> to,
          'base -> base
        ).as(simple *)
  }

  def deleteById(id: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update agentadinschool set status=0 where agent_id={base} and uid={id} and status=1")
        .on(
          'id -> id,
          'base -> base
        ).executeUpdate()
  }

  def published(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select a.* from agentadinschool s, agentadvertisement a where s.ad_id=a.uid and s.school_id={kg} " +
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
      get[Long]("ad_id") ~
      get[String]("school_id") ~
      get[Int]("publish_status") ~
      get[Option[Long]]("updated_at") map {
      case id ~ agent ~ ad ~ kg ~ publish ~ time =>
        AgentSchoolAd(Some(id), agent, ad, kg.toLong, time, publish)
    }
  }
}
