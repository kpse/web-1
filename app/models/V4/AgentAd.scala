package models.V4

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class AdPublishing(publish_status: Int, published_at: Option[Long], reject_reason: Option[String] = None)

case class AgentAd(id: Option[Long], agent_id: Long, title: String, address: Option[String], contact: String, time_span: Option[String],
                   detail: Option[String], logo: Option[String], updated_at: Option[Long], publishing: Option[AdPublishing] = None) {
  def preview(agentId: Long) = DB.withConnection {
    implicit c =>
      SQL("update agentadvertisement set publish_status=99, updated_at={time} where uid={id} and agent_id={base}")
        .on(
          'id -> id,
          'base -> agentId,
          'time -> System.currentTimeMillis()
        ).executeUpdate()
  }

  def publish(agentId: Long) = DB.withConnection {
    implicit c =>
      SQL("update agentadvertisement set publish_status=2, published_at={time}, updated_at={time} where uid={id} and agent_id={base} and publish_status=99")
        .on(
          'id -> id,
          'base -> agentId,
          'time -> System.currentTimeMillis()
        ).executeUpdate()
  }

  def reject(agentId: Long) = DB.withConnection {
    implicit c =>
      SQL("update agentadvertisement set publish_status=3, reject_reason={reason}, updated_at={time} where uid={id} and agent_id={base} and publish_status=99")
        .on(
          'id -> id,
          'base -> agentId,
          'reason -> publishing.map(_.reject_reason),
          'time -> System.currentTimeMillis()
        ).executeUpdate()
  }

  def update(base: Long): Option[AgentAd] = DB.withConnection {
    implicit c =>
      SQL("update agentadvertisement set agent_id={base}, title={title}, address={address}, contact={contact}, time_span={time_span}," +
        "detail={detail}, logo={logo}, publish_status={publish_status}, published_at={published_at}, updated_at={time} where uid={id}")
        .on(
          'id -> id,
          'base -> base,
          'title -> title,
          'address -> address,
          'contact -> contact,
          'time_span -> time_span,
          'detail -> detail,
          'logo -> logo,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (AgentAd.show(_, base))
  }

  def create(base: Long): Option[AgentAd] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into agentadvertisement (agent_id, title, address, contact, time_span, detail, logo, updated_at, created_at) values (" +
        "{base}, {title}, {address}, {contact}, {time_span}, {detail}, {logo}, {time}, {time})")
        .on(
          'base -> base,
          'title -> title,
          'address -> address,
          'contact -> contact,
          'time_span -> time_span,
          'detail -> detail,
          'logo -> logo,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (AgentAd.show(_, base))
  }
}

object AgentAd {
  implicit val writeAdPublishing = Json.writes[AdPublishing]
  implicit val readAdPublishing = Json.reads[AdPublishing]
  implicit val writeAgentAd = Json.writes[AgentAd]
  implicit val readAgentAd = Json.reads[AgentAd]

  def show(id: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from agentadvertisement where agent_id={base} and uid={id} and status=1")
        .on(
          'id -> id,
          'base -> base
        ).as(simple singleOpt)
  }

  def index(base: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from agentadvertisement where agent_id={base} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'from -> from,
          'to -> to,
          'base -> base
        ).as(simple *)
  }

  def publishStatus(id: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select publish_status, published_at, reject_reason from agentadvertisement where agent_id={base} and status=1 and uid={id}")
        .on(
          'id -> id,
          'base -> base
        ).as(simplePublishing single)
  }

  def deleteById(id: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update agentadvertisement set status=0 where agent_id={base} and uid={id} and status=1")
        .on(
          'id -> id,
          'base -> base
        ).executeUpdate()
  }

  val simple = {
    get[Long]("uid") ~
      get[Long]("agent_id") ~
      get[String]("title") ~
      get[Option[String]]("address") ~
      get[String]("contact") ~
      get[Option[String]]("time_span") ~
      get[Option[String]]("detail") ~
      get[Option[String]]("logo") ~
      get[Option[Long]]("updated_at") map {
      case id ~ agent ~ title ~ address ~ contact ~ timeSpan ~ detail ~ logo ~ time =>
        AgentAd(Some(id), agent, title, address, contact, timeSpan, detail, logo, time, Some(publishStatus(id, agent)))
    }
  }

  val simplePublishing = {
    get[Int]("publish_status") ~
      get[Option[String]]("reject_reason") ~
      get[Option[Long]]("published_at") map {
      case publish ~ reason ~ time =>
        AdPublishing(publish, time, reason)
    }
  }
}
