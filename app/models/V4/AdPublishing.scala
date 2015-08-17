package models.V4

import anorm.SqlParser._
import anorm._
import play.Logger
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class AdPublishing(publish_status: Int, published_at: Option[Long], reject_reason: Option[String] = None) {
  def active(table: String)(agentId: Long, target: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update $table set publish_status=4, updated_at={time} where uid={id} and agent_id={base} and publish_status in (2, 5)")
        .on(
          'id -> target,
          'base -> agentId,
          'time -> System.currentTimeMillis()
        ).executeUpdate()
  }

  def deactive(table: String)(agentId: Long, target: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update $table set publish_status=5, updated_at={time} where uid={id} and agent_id={base} and publish_status in (2, 4)")
        .on(
          'id -> target,
          'base -> agentId,
          'time -> System.currentTimeMillis()
        ).executeUpdate()
  }

  def preview(table: String)(agentId: Long, target: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update $table set publish_status=99, updated_at={time} where uid={id} and agent_id={base} and publish_status in (0, 3, 5)")
        .on(
          'id -> target,
          'base -> agentId,
          'time -> System.currentTimeMillis()
        ).executeUpdate()
  }

  def publish(table: String)(agentId: Long, target: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update $table set publish_status=2, published_at={time}, updated_at={time} where uid={id} and agent_id={base} and publish_status in (99, 3)")
        .on(
          'id -> target,
          'base -> agentId,
          'time -> System.currentTimeMillis()
        ).executeUpdate()
  }

  def reject(table: String)(agentId: Long, target: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update $table set publish_status=3, reject_reason={reason}, updated_at={time} where uid={id} and agent_id={base} and publish_status in (99, 2)")
        .on(
          'id -> target,
          'base -> agentId,
          'reason -> reject_reason,
          'time -> System.currentTimeMillis()
        ).executeUpdate()
  }
}

object AdPublishing {
  implicit val writeAdPublishing = Json.writes[AdPublishing]
  implicit val readAdPublishing = Json.reads[AdPublishing]

  def publishStatus(table: String)(id: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select publish_status, published_at, reject_reason from $table where agent_id={base} and status=1 and uid={id}")
        .on(
          'id -> id,
          'base -> base
        ).as(simple single)
  }

  val simple = {
      get[Int]("publish_status") ~
      get[Option[String]]("reject_reason") ~
      get[Option[Long]]("published_at") map {
      case publish ~ reason ~ time =>
        AdPublishing(publish, time, reason)
    }
  }
}