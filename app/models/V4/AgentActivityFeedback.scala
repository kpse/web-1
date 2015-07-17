package models.V4

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.Logger
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class AgentActivityFeedback(id: Option[Long], agent_id: Long, activity_id: Long, school_id: Long, parent_id: String,
                                 contact: String, name: String, updated_at: Option[Long]) {
  def create(kg: Long, activityId: Long) = DB.withConnection {
    implicit c =>
      SQL("INSERT INTO agentactivityfeedback (agent_id, activity_id, school_id, parent_id, contact, name, updated_at) VALUES" +
        "({agent}, {activity}, {kg}, {parent}, {contact}, {name}, {time})")
        .on(
          'agent -> agent_id,
          'activity -> activityId,
          'kg -> kg,
          'parent -> parent_id,
          'contact -> contact,
          'name -> name,
          'time -> System.currentTimeMillis
        ).executeInsert()
  }
}

object AgentActivityFeedback {
  implicit val writeAgentActivityFeedback = Json.writes[AgentActivityFeedback]
  implicit val readAgentActivityFeedback = Json.reads[AgentActivityFeedback]

  def index(agentId: Long, activityId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from agentactivityfeedback where agent_id={agent} and activity_id={activity} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'from -> from,
          'to -> to,
          'agent -> agentId,
          'activity -> activityId
        ).as(simple *)
  }

  val simple = {
    get[Long]("uid") ~
      get[Long]("agent_id") ~
      get[Long]("activity_id") ~
      get[String]("school_id") ~
      get[String]("parent_id") ~
      get[String]("contact") ~
      get[String]("name") ~
      get[Option[Long]]("updated_at") map {
      case id ~ agent ~ activity ~ school ~ parent ~ contact ~ name ~ time =>
        AgentActivityFeedback(Some(id), agent, activity, school.toLong, parent, contact, name, time)
    }
  }
}
