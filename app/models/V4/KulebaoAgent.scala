package models.V4

import models.helper.MD5Helper._
import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}
import play.Logger
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current
import models.LoginAccount
import play.api.mvc.Session

case class AgentStatistics(id: Long, agent: Long, school_id: Long, month: String, logged_once: Long, logged_ever: Long, created_at: Long)

case class KulebaoAgent(id: Option[Long], name: Option[String], area: Option[String], phone: Option[String], logo: Option[String], contact_info: Option[String], memo: Option[String],
                        login_name: Option[String], updated_at: Option[Long], created_at: Option[Long], expire: Option[Long], privilege_group: Option[String] = Some("agent")) extends LoginAccount {

  override def url(): String = "/agent"

  override def session(): Session = Session(Map("username" -> login_name.getOrElse(""), "phone" -> phone.getOrElse(""), "name" -> name.getOrElse(""), "id" -> id.getOrElse(0).toString))

  def removeDuplicatedPhone(phone: Option[String]) = DB.withConnection {
    implicit c =>
      phone foreach {
        p =>
          SQL("delete from agentinfo where phone={phone} and status=0")
            .on('phone -> p).execute()
      }
  }

  def update: Option[KulebaoAgent] = DB.withConnection {
    implicit c =>
      removeDuplicatedPhone(phone)
      SQL("update agentinfo set name={name}, phone={phone}, area={area}, logo_url={logo_url}, expire_at={expire_at}, " +
        "contact_info={contact_info}, memo={memo}, login_name={login_name}, " +
        "updated_at={time} where uid={id}")
        .on(
          'id -> id,
          'name -> name,
          'area -> area,
          'phone -> phone,
          'logo_url -> logo,
          'login_name -> login_name,
          'contact_info -> contact_info,
          'memo -> memo,
          'expire_at -> expire,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap KulebaoAgent.show
  }

  def create: Option[KulebaoAgent] = DB.withConnection {
    implicit c =>
      removeDuplicatedPhone(phone)
      val insert: Option[Long] = SQL("insert into agentinfo (name, area, phone, logo_url, login_password, login_name, updated_at, created_at, expire_at, memo, contact_info) values (" +
        "{name}, {area}, {phone}, {logo_url}, {login_password}, {login_name}, {time}, {time}, {expire_at}, {memo}, {contact_info})")
        .on(
          'name -> name,
          'area -> area,
          'phone -> phone,
          'logo_url -> logo,
          'login_password -> md5(phone.drop(3).toString()),
          'login_name -> login_name,
          'contact_info -> contact_info,
          'memo -> memo,
          'expire_at -> expire,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap KulebaoAgent.show
  }
}

object KulebaoAgent {
  implicit val writeKulebaoAgent = Json.writes[KulebaoAgent]
  implicit val readKulebaoAgent = Json.reads[KulebaoAgent]

  implicit val writeAgentStatistics = Json.writes[AgentStatistics]
  implicit val readAgentStatistics = Json.reads[AgentStatistics]

  def show(id: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from agentinfo where uid={id} and status=1")
        .on(
          'id -> id
        ).as(simple singleOpt)
  }

  def index(from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from agentinfo where status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'from -> from,
          'to -> to,
          'most -> most
        ).as(simple *)
  }

  def deleteById(id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update agentinfo set status=0 where uid={id} and status=1")
        .on(
          'id -> id
        ).executeUpdate()
  }

  def authenticate(loginName: String, password: String) = DB.withConnection {
    implicit c =>
      SQL("select * from agentinfo where login_name={login} and login_password={password} and status=1")
        .on(
          'login -> loginName,
          'password -> md5(password)
        ).as(simple singleOpt)
  }

  def isAgent(id: Long, path: String) = DB.withConnection {
    implicit c =>
      val isAgent: Boolean = SQL("select count(1) from agentinfo where uid={id} and status=1")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
      Logger.info(s"request path is $path")
      isAgent && (path.matches(s"^(?:/api/v\\d+)?/agent/$id(/.+)?") || path.matches(s"^/agent#/main/$id(/.+)?")) ||
        path.matches("^/agent") || path.matches(s"^/main/$id(/.+)?")
  }

  def duplicatedPhone(agent: KulebaoAgent) = DB.withConnection {
    implicit c =>
      agent.id match {
        case Some(i) =>
          SQL("select count(1) from agentinfo where uid<>{id} and phone={phone} and status=1")
            .on(
              'id -> i,
              'phone -> agent.phone
            ).as(get[Long]("count(1)") single) > 0
        case None =>
          SQL("select count(1) from agentinfo where phone={phone} and status=1")
            .on(
              'phone -> agent.phone
            ).as(get[Long]("count(1)") single) > 0
      }

  }

  def stats(agentId: Long) = DB.withConnection {
    implicit c =>
      val pattern: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMM")
      Logger.info(s"from = ${pattern.print(DateTime.now().minusMonths(13))}")
      Logger.info(s"to = ${pattern.print(DateTime.now().minusMonths(1))}")
      SQL(s"select * from agentstatistics where month >= {from} and month <= {to} and agent_id={agent}")
        .on(
          'agent -> agentId,
          'from -> pattern.print(DateTime.now().minusMonths(13)),
          'to -> pattern.print(DateTime.now().minusMonths(1))
        ).as(simpleStatistics *)
  }

  def collectData(data: AgentStatistics) = DB.withConnection {
    implicit c =>
      val pattern: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMM")
      Logger.info(s"lastMonth = ${pattern.print(DateTime.now().minusMonths(1))}")
      historyDataExists(data) match {
        case false =>
          SQL(s"insert into agentstatistics (agent_id, school_id, month, logged_once, logged_ever, created_at) values " +
            s"({agent}, {school_id}, {month}, {once}, {ever}, {time})")
            .on(
              'agent -> data.agent,
              'school_id -> data.school_id,
              'month -> data.month,
              'once -> data.logged_once,
              'ever -> data.logged_ever,
              'time -> System.currentTimeMillis()
            ).executeInsert()
        case true => 0
      }
  }

  def collectTheWholeMonth(agent: Long, school_id: Long, lastMonth: DateTime): AgentStatistics = DB.withConnection {
    implicit c =>
      val firstMilli = lastMonth.withDayOfMonth(1).withMillisOfDay(1).getMillis
      val lastMilli = lastMonth.plusMonths(1).withDayOfMonth(1).withMillisOfDay(1).getMillis
      Logger.info(s"collectTheWholeMonth firstMilli = $firstMilli")
      Logger.info(s"collectTheWholeMonth lastMilli = $lastMilli")
      val loggedOnce: Long = SQL(s"SELECT count(distinct phone) count FROM bindinghistory where phone in (select phone from parentinfo where school_id={kg} and status=1) and updated_at > {begin} and updated_at < {end}")
        .on(
          'kg -> school_id,
          'begin -> firstMilli,
          'end -> lastMilli
        ).as(get[Long]("count") single)
      val loggedEver: Long = SQL(s"SELECT count(distinct phone) count FROM bindinghistory where phone in (select phone from parentinfo where school_id={kg} and status=1) and updated_at < {end}")
        .on(
          'kg -> school_id,
          'begin -> firstMilli,
          'end -> lastMilli
        ).as(get[Long]("count") single)
      val pattern: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMM")
      AgentStatistics(0, agent, school_id, pattern.print(lastMonth), loggedOnce, loggedEver, 0)

  }

  def historyDataExists(data: AgentStatistics): Boolean = DB.withConnection {
    implicit c =>
      SQL(s"select count(1) from agentstatistics where agent_id={agent} and school_id={kg} and month={month}")
        .on('agent -> data.agent, 'kg -> data.school_id, 'month -> data.month).as(get[Long]("count(1)") single) > 0
  }

  val simple = {
    get[Long]("uid") ~
      get[Option[String]]("name") ~
      get[Option[String]]("area") ~
      get[Option[String]]("phone") ~
      get[Option[String]]("logo_url") ~
      get[Option[String]]("login_name") ~
      get[Option[Long]]("expire_at") ~
      get[Option[String]]("contact_info") ~
      get[Option[String]]("memo") ~
      get[Option[Long]]("updated_at") ~
      get[Option[Long]]("created_at") map {
      case id ~ name ~ area ~ phone ~ url ~ loginName ~ expire ~ contact_info ~ memo ~ updated ~ created =>
        KulebaoAgent(Some(id), name, area, phone, url, contact_info, memo, loginName, updated, created, expire)
    }
  }

  val simpleStatistics = {
    get[Long]("uid") ~
      get[Long]("agent_id") ~
      get[String]("school_id") ~
      get[String]("month") ~
      get[Long]("logged_once") ~
      get[Long]("logged_ever") ~
      get[Long]("created_at") map {
      case id ~ agent ~ school ~ month ~ once ~ ever ~ created =>
        AgentStatistics(id, agent, school.toLong, month, once, ever, created)
    }
  }
}
