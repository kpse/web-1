package models.V4

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class AgentAd(id: Option[Long], agent_id: Long, title: String, address: Option[String],
                   contact: String, time_span: Option[String], detail: Option[String], logo: Option[String], updated_at: Option[Long]) {
  def update(base: Long): Option[AgentAd] = DB.withConnection {
    implicit c =>
      SQL("update agentadvertisement set agent_id={base}, title={title}, address={address}, contact={contact}, time_span={time_span}," +
        "detail={detail}, logo={logo}, updated_at={time} where uid={id}")
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
        AgentAd(Some(id), agent, title, address, contact, timeSpan, detail, logo, time)
    }
  }
}
