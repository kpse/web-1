package models.V4

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class AgentActivityEnrollment(id: Option[Long], agent_id: Long, activity_id: Long, school_id: Long, parent_id: String,
                                 contact: String, name: String, updated_at: Option[Long]) {
  def create(kg: Long, activityId: Long) = DB.withConnection {
    implicit c =>
      SQL("INSERT INTO agentactivityenrollment (agent_id, activity_id, school_id, parent_id, contact, name, updated_at) VALUES" +
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

object AgentActivityEnrollment {
  implicit val writeAgentActivityEnrollment = Json.writes[AgentActivityEnrollment]
  implicit val readAgentActivityEnrollment = Json.reads[AgentActivityEnrollment]

  def index(agentId: Long, activityId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from agentactivityenrollment where agent_id={agent} and activity_id={activity} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'from -> from,
          'to -> to,
          'agent -> agentId,
          'activity -> activityId
        ).as(simple *)
  }

  def show(kg: Long, activityId: Long, parentId: String) = DB.withConnection {
    implicit c =>
      SQL(s"select * from agentactivityenrollment where activity_id={activity} and status=1 and parent_id={parent} limit 1")
        .on(
          'kg -> kg,
          'activity -> activityId,
          'parent -> parentId
        ).as(simple singleOpt)
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
        AgentActivityEnrollment(Some(id), agent, activity, school.toLong, parent, contact, name, time)
    }
  }
}
