package models.V4

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.Logger
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class AgentActivity(id: Option[Long], agent_id: Long, contractor_id: Long, title: String, address: Option[String], contact: String, time_span: Option[String],
                         detail: Option[String], logo: Option[String], updated_at: Option[Long], publishing: Option[AdPublishing] = None) {
  def preview(agentId: Long) = for {i <- id
                                    p <- publishing} yield p.preview(AgentActivity.tableName)(agentId, i)


  def publish(agentId: Long) = for {i <- id
                                    p <- publishing} yield p.publish(AgentActivity.tableName)(agentId, i)

  def reject(agentId: Long) = for {i <- id
                                   p <- publishing} yield p.reject(AgentActivity.tableName)(agentId, i)

  def update(base: Long, contractorId: Long): Option[AgentActivity] = DB.withConnection {
    implicit c =>
      SQL("update agentactivity set agent_id={base}, contractor_id={contractor}, title={title}, address={address}, contact={contact}, time_span={time_span}," +
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
          'contractor -> contractorId,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (AgentActivity.show(_, base, contractorId))
  }

  def create(base: Long, contractorId: Long): Option[AgentActivity] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into agentactivity (agent_id, contractor_id, title, address, contact, time_span, detail, logo, updated_at, created_at, publish_status) values (" +
        "{base}, {contractor}, {title}, {address}, {contact}, {time_span}, {detail}, {logo}, {time}, {time}, 0)")
        .on(
          'base -> base,
          'title -> title,
          'address -> address,
          'contact -> contact,
          'time_span -> time_span,
          'detail -> detail,
          'logo -> logo,
          'contractor -> contractorId,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (AgentActivity.show(_, base, contractorId))
  }
}

object AgentActivity {
  implicit val writeAgentAd = Json.writes[AgentActivity]
  implicit val readAgentAd = Json.reads[AgentActivity]
  val tableName: String = "agentactivity"

  def show(id: Long, base: Long, contractorId: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from agentactivity where agent_id={base} and uid={id} and status=1 and contractor_id={contractor}")
        .on(
          'id -> id,
          'contractor -> contractorId,
          'base -> base
        ).as(simple singleOpt)
  }

  def index(base: Long, contractorId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from agentactivity where agent_id={base} and contractor_id={contractor} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'from -> from,
          'to -> to,
          'contractor -> contractorId,
          'base -> base
        ).as(simple *)
  }

  def deleteById(id: Long, base: Long, contractorId: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update agentactivity set status=0 where agent_id={base} and uid={id} and status=1 and contractor_id={contractor}")
        .on(
          'id -> id,
          'contractor -> contractorId,
          'base -> base
        ).executeUpdate()
  }

  val simple = {
    get[Long]("uid") ~
      get[Long]("agent_id") ~
      get[Long]("contractor_id") ~
      get[String]("title") ~
      get[Option[String]]("address") ~
      get[String]("contact") ~
      get[Option[String]]("time_span") ~
      get[Option[String]]("detail") ~
      get[Option[String]]("logo") ~
      get[Option[Long]]("updated_at") map {
      case id ~ agent ~ contractor ~ title ~ address ~ contact ~ timeSpan ~ detail ~ logo ~ time =>
        AgentActivity(Some(id), agent, contractor, title, address, contact, timeSpan, detail, logo, time, Some(AdPublishing.publishStatus(tableName)(id, agent)))
    }
  }
}
