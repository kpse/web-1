package models.V4

import models.helper.MD5Helper._
import models.helper.PasswordHelper._
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
import models.V4.SchoolOperationReport.writeSchoolOperationReport
import models.V4.SchoolOperationReport.readSchoolOperationReport
import play.api.mvc.Session

case class AgentStatistics(id: Long, agent: Long, data: SchoolOperationReport)

case class KulebaoAgent(id: Option[Long], name: Option[String], area: Option[String], phone: Option[String], logo: Option[String], contact_info: Option[String], memo: Option[String], city: Option[String],
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
      SQL("update agentinfo set name={name}, phone={phone}, area={area}, city={city}, logo_url={logo_url}, expire_at={expire_at}, " +
        "contact_info={contact_info}, memo={memo}, login_name={login_name}, " +
        "updated_at={time} where uid={id}")
        .on(
          'id -> id,
          'name -> name,
          'city -> city,
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
      val insert: Option[Long] = SQL("insert into agentinfo (name, area, city, phone, logo_url, login_password, login_name, updated_at, created_at, expire_at, memo, contact_info) values (" +
        "{name}, {area}, {city}, {phone}, {logo_url}, {login_password}, {login_name}, {time}, {time}, {expire_at}, {memo}, {contact_info})")
        .on(
          'name -> name,
          'city -> city,
          'area -> area,
          'phone -> phone,
          'logo_url -> logo,
          'login_password -> generateNewPassword(phone.getOrElse("secret")),
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

  val pattern: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMM")

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

  def deleteById(id: Long) = DB.withTransaction {
    implicit c =>
      try {
        List("agentschool", "agentcontractor", "agentcontractorinschool", "agentactivity", "agentactivityenrollment",
          "agentactivityinschool").map(removeAgentFromTable(_, id))
        SQL(s"update agentinfo set status=0 where uid={id} and status=1")
          .on(
            'id -> id
          ).executeUpdate()
        c.commit()
      }
      catch {
        case t: Throwable => c.rollback()
          Logger.warn(t.getLocalizedMessage)
      }
  }

  def removeAgentFromTable(name: String, id: Long): Int = DB.withTransaction {
    implicit c =>
      SQL(s"update $name set status=0 where agent_id={id} and status=1")
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

  def isAgent(id: String, path: String): Boolean = DB.withConnection {
    implicit c =>
      if (id.contains("_")) return false
      val isAgent: Boolean = SQL("select count(1) from agentinfo where uid={id} and status=1")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
      Logger.debug(s"request path is $path")
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

  private val lastMonth: DateTime = DateTime.now().minusMonths(1)

  def stats(agentId: Long) = DB.withConnection {
    implicit c =>
      Logger.debug(s"from = ${pattern.print(DateTime.now().minusMonths(12))}")
      Logger.debug(s"to = ${pattern.print(lastMonth)}")
      SQL(s"select * from agentstatistics where month >= {from} and month <= {to} and agent_id={agent}")
        .on(
          'agent -> agentId,
          'from -> pattern.print(DateTime.now().minusMonths(12)),
          'to -> pattern.print(lastMonth)
        ).as(simpleStatistics *)
  }

  def deleteStats(agentId: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"delete from agentstatistics where agent_id={agent} and uid={id}")
        .on(
          'agent -> agentId,
          'id -> id
        ).execute()
  }

  def collectData(agentData: AgentStatistics) = DB.withConnection {
    implicit c =>
      Logger.debug(s"lastMonth = ${pattern.print(lastMonth)}")
      SQL(s"insert into agentstatistics (agent_id, school_id, month, child_count, parent_count, logged_once, logged_ever, created_at) values " +
        s"({agent}, {school_id}, {month}, {child}, {parent}, {once}, {ever}, {time})")
        .on(
          'agent -> agentData.agent,
          'school_id -> agentData.data.school_id,
          'month -> agentData.data.month,
          'child -> agentData.data.child_count,
          'parent -> agentData.data.parent_count,
          'once -> agentData.data.logged_once,
          'ever -> agentData.data.logged_ever,
          'time -> System.currentTimeMillis()
        ).executeInsert()
  }

  def historyDataExists(agent: Long, kg: Long, month: DateTime): Boolean = DB.withConnection {
    implicit c =>
      SQL(s"select count(1) from agentstatistics where agent_id={agent} and school_id={kg} and month={month}")
        .on('agent -> agent, 'kg -> kg, 'month -> pattern.print(month)).as(get[Long]("count(1)") single) > 0
  }

  def monthlyStatistics = {
    index(None, None, None).foreach {
      case agent =>
        AgentSchool.index(agent.id.get, None, None, None).foreach {
          case school =>
            historyDataExists(agent.id.get, school.school_id, lastMonth) match {
              case false =>
                val month = SchoolOperationReport.timePeriodOfMonth(lastMonth)
                val monthData: SchoolOperationReport = SchoolOperationReport.collectInGivenTimePeriod(school.school_id, month._1, month._2)
                Logger.info(s"insert data ${monthData.logged_once}, ${monthData.logged_ever} for agent ${agent.id.get} ${pattern.print(lastMonth)} in ${monthData.school_id}")
                collectData(AgentStatistics(monthData.id, agent.id.get, monthData))
              case true =>
                Logger.info(s"no data insertion for ${pattern.print(lastMonth)} in ${school.school_id} is needed.")
            }
        }
    }
  }

  val simple = {
    get[Long]("uid") ~
      get[Option[String]]("name") ~
      get[Option[String]]("area") ~
      get[Option[String]]("city") ~
      get[Option[String]]("phone") ~
      get[Option[String]]("logo_url") ~
      get[Option[String]]("login_name") ~
      get[Option[Long]]("expire_at") ~
      get[Option[String]]("contact_info") ~
      get[Option[String]]("memo") ~
      get[Option[Long]]("updated_at") ~
      get[Option[Long]]("created_at") map {
      case id ~ name ~ area ~ city ~ phone ~ url ~ loginName ~ expire ~ contact_info ~ memo ~ updated ~ created =>
        KulebaoAgent(Some(id), name, area, phone, url, contact_info, memo, city, loginName, updated, created, expire)
    }
  }

  val simpleStatistics = {
    get[Long]("uid") ~
      get[Long]("agent_id") ~
      get[String]("school_id") ~
      get[String]("month") ~
      get[Long]("logged_once") ~
      get[Long]("logged_ever") ~
      get[Long]("child_count") ~
      get[Long]("parent_count") ~
      get[Long]("created_at") map {
      case id ~ agent ~ school ~ month ~ once ~ ever ~ child ~ parent ~ created =>
        val data: SchoolOperationReport = SchoolOperationReport(id, school.toLong, month, month + "01", once, ever, created, child, parent)
        AgentStatistics(id, agent, data)
    }
  }
}

object AgentWithLoginName {
  def unapply(loginName: String) = DB.withConnection {
    implicit c =>
      SQL("select * from agentinfo where login_name={login_name} and status=1")
        .on(
          'login_name -> loginName
        ).as(KulebaoAgent.simple singleOpt)
  }
}

