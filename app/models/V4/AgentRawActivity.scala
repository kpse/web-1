package models.V4

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.Logger
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current


case class ActivityPrice(origin: Double, discounted: Double)
case class HeroImage(url: String)
case class AgentRawActivity(id: Option[Long], agent_id: Long, contractor_id: Option[Long], title: String,
                            address: Option[String], contact: String, time_span: Option[String],
                         detail: Option[String], logos: List[HeroImage], updated_at: Option[Long],
                            publishing: Option[AdPublishing] = None, location: Option[EarthLocation] = None,
                             price: Option[ActivityPrice] = None, priority: Long = 0) {
  def deactive(agentId: Long) = for {i <- id
                                     p <- publishing} yield p.deactive(AgentRawActivity.tableName)(agentId, i)

  def active(agentId: Long) = for {i <- id
                                   p <- publishing} yield p.active(AgentRawActivity.tableName)(agentId, i)

  def preview(agentId: Long) = for {i <- id
                                    p <- publishing} yield p.preview(AgentRawActivity.tableName)(agentId, i)


  def publish(agentId: Long) = for {i <- id
                                    p <- publishing} yield p.publish(AgentRawActivity.tableName)(agentId, i)

  def reject(agentId: Long) = for {i <- id
                                   p <- publishing} yield p.reject(AgentRawActivity.tableName)(agentId, i)

  def update(base: Long): Option[AgentRawActivity] = DB.withConnection {
    implicit c =>
      SQL("update agentactivity set agent_id={base}, contractor_id={contractor}, title={title}, address={address}, " +
        "contact={contact}, time_span={time_span}, detail={detail}, latitude={latitude}, longitude={longitude}, logo={logo}, " +
        "origin_price={origin_price}, price={price}, geo_address={geo_address}, priority={priority}, updated_at={time} where uid={id}")
        .on(
          'id -> id,
          'base -> base,
          'title -> title,
          'address -> address,
          'contact -> contact,
          'time_span -> time_span,
          'detail -> detail,
          'latitude -> location.map (_.latitude.bigDecimal),
          'longitude -> location.map (_.longitude.bigDecimal),
          'geo_address -> location.map (_.address),
          'origin_price -> price.map (_.origin),
          'price -> price.map (_.discounted),
          'logo -> logos.map(_.url).mkString(AgentRawActivity.urlSeparator),
          'priority -> priority,
          'contractor -> contractor_id,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (AgentRawActivity.show(_, base))
  }

  def create(base: Long): Option[AgentRawActivity] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into agentactivity (agent_id, contractor_id, title, address, contact, " +
        "time_span, detail, logo, updated_at, created_at, publish_status, latitude, longitude, geo_address, origin_price, price, priority) values (" +
        "{base}, {contractor}, {title}, {address}, {contact}, {time_span}, {detail}, {logo}, {time}, {time}, 0, {latitude}, {longitude}, {geo_address}, {origin_price}, {price}, {priority})")
        .on(
          'base -> base,
          'title -> title,
          'address -> address,
          'contact -> contact,
          'time_span -> time_span,
          'detail -> detail,
          'latitude -> location.map (_.latitude.bigDecimal),
          'longitude -> location.map (_.longitude.bigDecimal),
          'geo_address -> location.map (_.address),
          'origin_price -> price.map (_.origin),
          'price -> price.map (_.discounted),
          'logo -> logos.map(_.url).mkString(AgentRawActivity.urlSeparator),
          'priority -> priority,
          'contractor -> contractor_id,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (AgentRawActivity.show(_, base))
  }
}

object AgentRawActivity {
  implicit val writeEarthLocation = Json.writes[EarthLocation]
  implicit val readEarthLocation = Json.reads[EarthLocation]
  implicit val writeActivityPrice = Json.writes[ActivityPrice]
  implicit val readActivityPrice = Json.reads[ActivityPrice]
  implicit val writeHeroImage = Json.writes[HeroImage]
  implicit val readHeroImage = Json.reads[HeroImage]
  implicit val writeAgentRawActivity = Json.writes[AgentRawActivity]
  implicit val readAgentRawActivity = Json.reads[AgentRawActivity]
  val tableName: String = "agentactivity"
  val urlSeparator = "  "

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


  def priceOfActivity(originPrice: Option[Double], price: Option[Double]): Option[ActivityPrice] =
    for(origin <- originPrice;price <- price) yield ActivityPrice(origin, price)

  def splitLogos(logo: Option[String]): List[HeroImage] = logo match {
    case Some(images) if images.length > 0 => images.split(urlSeparator).map(url => HeroImage(url)).toList
    case _ => List()
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
      get[Option[java.math.BigDecimal]]("latitude") ~
      get[Option[java.math.BigDecimal]]("longitude") ~
      get[Option[Double]]("origin_price") ~
      get[Option[Double]]("price") ~
      get[Option[String]]("logo") ~
      get[Option[Long]]("updated_at") ~
      get[Long]("priority") ~
      get[Option[String]]("geo_address") map {
      case id ~ agent ~ contractor ~ title ~ address ~ contact ~ timeSpan ~ detail ~ latitude ~ longitude ~ origin_price ~ price ~ logo ~ time ~ priority ~ geoAddress=>
        AgentRawActivity(Some(id), agent, contractor, title, address, contact, timeSpan, detail, splitLogos(logo), time,
          Some(AdPublishing.publishStatus(tableName)(id, agent)), AgentContractor.locationOfContractor(latitude, longitude, geoAddress), priceOfActivity(origin_price, price), priority)
    }
  }
}
