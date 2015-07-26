package models.V4

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.Logger
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class AgentRawActivity(id: Option[Long], agent_id: Long, contractor_id: Option[Long], title: String, address: Option[String], contact: String, time_span: Option[String],
                         detail: Option[String], logo: Option[String], updated_at: Option[Long], publishing: Option[AdPublishing] = None, location: Option[EarthLocation] = None) {
  def deactive(agentId: Long) = for {i <- id
                                     p <- publishing} yield p.deactive(AgentActivity.tableName)(agentId, i)

  def active(agentId: Long) = for {i <- id
                                   p <- publishing} yield p.active(AgentActivity.tableName)(agentId, i)

  def preview(agentId: Long) = for {i <- id
                                    p <- publishing} yield p.preview(AgentActivity.tableName)(agentId, i)


  def publish(agentId: Long) = for {i <- id
                                    p <- publishing} yield p.publish(AgentActivity.tableName)(agentId, i)

  def reject(agentId: Long) = for {i <- id
                                   p <- publishing} yield p.reject(AgentActivity.tableName)(agentId, i)

  def update(base: Long): Option[AgentRawActivity] = DB.withConnection {
    implicit c =>
      SQL("update agentactivity set agent_id={base}, contractor_id={contractor}, title={title}, address={address}, contact={contact}, time_span={time_span}," +
        "detail={detail}, latitude={latitude}, longitude={longitude}, logo={logo}, updated_at={time} where uid={id}")
        .on(
          'id -> id,
          'base -> base,
          'title -> title,
          'address -> address,
          'contact -> contact,
          'time_span -> time_span,
          'detail -> detail,
          'latitude -> location.map (_.latitude),
          'longitude -> location.map (_.longitude),
          'logo -> logo,
          'contractor -> contractor_id,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (AgentRawActivity.show(_, base))
  }

  def create(base: Long): Option[AgentRawActivity] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into agentactivity (agent_id, contractor_id, title, address, contact, " +
        "time_span, detail, logo, updated_at, created_at, publish_status, latitude, longitude) values (" +
        "{base}, {contractor}, {title}, {address}, {contact}, {time_span}, {detail}, {logo}, {time}, {time}, 0, {latitude}, {longitude})")
        .on(
          'base -> base,
          'title -> title,
          'address -> address,
          'contact -> contact,
          'time_span -> time_span,
          'detail -> detail,
          'latitude -> location.map (_.latitude),
          'longitude -> location.map (_.longitude),
          'logo -> logo,
          'contractor -> contractor_id,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (AgentRawActivity.show(_, base))
  }
}

object AgentRawActivity {
  import AgentContractor.writeEarthLocation
  import AgentContractor.readEarthLocation
  implicit val writeAgentRawActivity = Json.writes[AgentRawActivity]
  implicit val readAgentRawActivity = Json.reads[AgentRawActivity]
  val tableName: String = "agentactivity"

  def show(id: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from agentactivity where agent_id={base} and uid={id} and status=1")
        .on(
          'id -> id,
          'base -> base
        ).as(simple singleOpt)
  }

  def index(base: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from agentactivity where agent_id={base} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'from -> from,
          'to -> to,
          'base -> base
        ).as(simple *)
  }

  def deleteById(id: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update agentactivity set status=0 where agent_id={base} and uid={id} and status=1")
        .on(
          'id -> id,
          'base -> base
        ).executeUpdate()
  }

  val simple = {
    get[Long]("uid") ~
      get[Long]("agent_id") ~
      get[Option[Long]]("contractor_id") ~
      get[String]("title") ~
      get[Option[String]]("address") ~
      get[String]("contact") ~
      get[Option[String]]("time_span") ~
      get[Option[String]]("detail") ~
      get[Option[Double]]("latitude") ~
      get[Option[Double]]("longitude") ~
      get[Option[String]]("logo") ~
      get[Option[Long]]("updated_at") map {
      case id ~ agent ~ contractor ~ title ~ address ~ contact ~ timeSpan ~ detail ~ latitude ~ longitude ~ logo ~ time =>
        AgentRawActivity(Some(id), agent, contractor, title, address, contact, timeSpan, detail, logo, time,
          Some(AdPublishing.publishStatus(tableName)(id, agent)), AgentContractor.locationOfContractor(latitude, longitude))
    }
  }
}
