package models.V4

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.Logger
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class AgentContractor(id: Option[Long], agent_id: Long, category: String, title: String, address: Option[String], contact: String, time_span: Option[String],
                           detail: Option[String], logo: Option[String], updated_at: Option[Long], publishing: Option[AdPublishing] = None) {

  def preview(agentId: Long) = for {i <- id
                                    p <- publishing} yield p.preview(AgentContractor.tableName)(agentId, i)


  def publish(agentId: Long) = for {i <- id
                                    p <- publishing} yield p.publish(AgentContractor.tableName)(agentId, i)

  def reject(agentId: Long) = for {i <- id
                                   p <- publishing} yield p.reject(AgentContractor.tableName)(agentId, i)


  def update(base: Long): Option[AgentContractor] = DB.withConnection {
    implicit c =>
      SQL("update agentcontractor set agent_id={base}, category={category}, title={title}, address={address}, contact={contact}, time_span={time_span}," +
        "detail={detail}, logo={logo}, updated_at={time} where uid={id}")
        .on(
          'id -> id,
          'base -> base,
          'title -> title,
          'category -> AgentContractor.categoryToEnum(category),
          'address -> address,
          'contact -> contact,
          'time_span -> time_span,
          'detail -> detail,
          'logo -> logo,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (AgentContractor.show(_, base))
  }

  def create(base: Long): Option[AgentContractor] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into agentcontractor (agent_id, category, title, address, contact, time_span, detail, logo, updated_at, created_at, publish_status) values (" +
        "{base}, {category}, {title}, {address}, {contact}, {time_span}, {detail}, {logo}, {time}, {time}, 0)")
        .on(
          'base -> base,
          'title -> title,
          'category -> AgentContractor.categoryToEnum(category),
          'address -> address,
          'contact -> contact,
          'time_span -> time_span,
          'detail -> detail,
          'logo -> logo,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (AgentContractor.show(_, base))
  }
}

object AgentContractor {
  implicit val writeAgentAd = Json.writes[AgentContractor]
  implicit val readAgentAd = Json.reads[AgentContractor]

  val tableName: String = "agentcontractor"

  def show(id: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from agentcontractor where agent_id={base} and uid={id} and status=1")
        .on(
          'id -> id,
          'base -> base
        ).as(simple singleOpt)
  }

  def index(base: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from agentcontractor where agent_id={base} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'from -> from,
          'to -> to,
          'base -> base
        ).as(simple *)
  }


  def deleteById(id: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update agentcontractor set status=0 where agent_id={base} and uid={id} and status=1")
        .on(
          'id -> id,
          'base -> base
        ).executeUpdate()
  }

  def categoryFromEnum(category: Int): String = category match {
    case 1 => "亲子摄影"
    case 2 => "培训教育"
    case 4 => "亲子游乐"
    case 8 => "亲子购物"
    case _ => "其他"
  }

  def categoryToEnum(category: String): Int = category match {
    case "亲子摄影" => 1
    case "培训教育" => 2
    case "亲子游乐" => 4
    case "亲子购物" => 8
    case "其他" => 0
  }

  val simple = {
    get[Long]("uid") ~
      get[Long]("agent_id") ~
      get[String]("title") ~
      get[Int]("category") ~
      get[Option[String]]("address") ~
      get[String]("contact") ~
      get[Option[String]]("time_span") ~
      get[Option[String]]("detail") ~
      get[Option[String]]("logo") ~
      get[Option[Long]]("updated_at") map {
      case id ~ agent ~ title ~ category ~ address ~ contact ~ timeSpan ~ detail ~ logo ~ time =>
        AgentContractor(Some(id), agent, categoryFromEnum(category), title, address, contact, timeSpan, detail, logo, time, Some(AdPublishing.publishStatus(tableName)(id, agent)))
    }
  }
}