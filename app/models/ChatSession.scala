package models

import anorm.SqlParser._
import anorm.{~, _}
import models.helper.RangerHelper._
import play.Logger
import play.api.Play
import play.api.Play.current
import play.api.db.DB

case class ChatSession(topic: String, timestamp: Option[Long], id: Option[Long], content: String, media: Option[MediaContent] = Some(MediaContent("")), sender: Sender, medium: Option[List[MediaContent]] = Some(List[MediaContent]()))

case class Sender(id: String, `type`: Option[String] = Some("t")) {
  def retrieveSchool: Option[School] = DB.withConnection {
    implicit c =>
      `type` match {
        case Some("p") =>
          Parent.idSearch(id).map(_.school_id) flatMap schoolById
        case Some("t") =>
          Employee.idSearch(id).map(_.school_id) flatMap schoolById
        case _ => None

      }
  }

  val simpleSchool = {
    get[String]("school_id") ~
      get[String]("name") map {
      case kg ~ name =>
        School(kg.toLong, name)
    }
  }

  def schoolById(kg: Long): Option[School] = DB.withConnection {
    implicit c =>
      SQL("select school_id, name from schoolinfo where school_id={kg}").on('kg -> kg).as(simpleSchool singleOpt)
  }
}

case class MediaContent(url: String, `type`: Option[String] = Some("image"))

case class SessionInMonth(month: String, count: Long)

object ChatSession {
  def employeeSessionHistory(kg: Long, employeeId: String, from: Option[Long], to: Option[Long], most: Option[Int], month: Option[String]) = DB.withConnection {
    implicit c =>
      SQL("select * from sessionlog where status=1 and school_id={kg} and sender={sender} and session_id not like 'h_%' " +
        generateMonthQuery(month) + rangerQuery(from, to, most))
        .on(
          'kg -> kg.toString,
          'sender -> employeeId,
          'from -> from,
          'to -> to,
          'month -> month
        ).as(simple() *)
  }

  def employeeHistory(kg: Long, employeeId: String, from: Option[Long], to: Option[Long], most: Option[Int], month: Option[String]) = DB.withConnection {
    implicit c =>
      SQL("select * from sessionlog where status=1 and school_id={kg} and sender={sender} and session_id like 'h_%' " +
        generateMonthQuery(month) + rangerQuery(from, to, most))
        .on(
          'kg -> kg.toString,
          'sender -> employeeId,
          'from -> from,
          'to -> to,
          'month -> month
        ).as(simple(Some("^h_")) *)
  }

  def allHistoryImages(kg: Long, timestamp: Long): List[String] = DB.withConnection {
    implicit c =>
      val urls: List[String] = SQL("SELECT media_url FROM sessionlog where school_id = {kg} and update_at > {time} and session_id like 'h_%' and media_type like '%image%'")
        .on('kg -> kg.toString, 'time -> timestamp)
        .as(get[String]("media_url") *)
      urls flatMap (_.split("  ")) filter (_.length > 0)
  }

  def countGrowingHistory(kg: Long) = DB.withConnection {
    implicit c =>
      val count: Long = SQL("select count(1) from sessionlog where school_id={kg} and session_id like 'h_%'").on('kg -> kg).as(get[Long]("count(1)") single)
      Statistics("session", count)
  }

  def countInSchool(kg: Long) = DB.withConnection {
    implicit c =>
      val count: Long = SQL("select count(1) from sessionlog where school_id={kg} and session_id not like 'h_%'").on('kg -> kg).as(get[Long]("count(1)") single)
      Statistics("session", count)
  }


  def adaptLocalDB(sql: String): String = Play.current.configuration.getString("db.default.driver") match {
    case Some("org.h2.Driver") =>
      sql.replaceAll( """DATE_FORMAT\(\(from_unixtime\(update_at / 1000\)\), '%Y%m'\)""", "FORMATDATETIME(DATEADD('SECOND', update_at/1000, DATE '1970-01-01'), 'YMM')")
        .replaceAll( """DATE_FORMAT\(\(FROM_UNIXTIME\(update_at / 1000\)\), '%Y'\)""", "FORMATDATETIME(DATEADD('SECOND', update_at/1000, DATE '1970-01-01'), 'Y')")
    case other => sql
  }

  def groupByMonth(kg: Long, topic: String, year: String) = DB.withConnection {
    implicit c =>
      val groupSql: String = "SELECT DATE_FORMAT((from_unixtime(update_at / 1000)), '%Y%m') month, count(1) FROM sessionlog " +
        " where session_id={topic} AND school_id={kg} AND DATE_FORMAT((FROM_UNIXTIME(update_at / 1000)), '%Y') = {year} " +
        " GROUP BY DATE_FORMAT((from_unixtime(update_at / 1000)), '%Y%m')"
      SQL(adaptLocalDB(groupSql))
        .on(
          'kg -> kg.toString,
          'topic -> topic,
          'year -> year
        ).as(statistic *)
  }

  val statistic = {
    get[String]("month") ~
      get[Long]("count(1)") map {
      case month ~ count =>
        SessionInMonth(month, count)
    }
  }

  def delete(kg: Long, topicId: String, id: Long) = DB.withConnection {
    implicit c =>
      SQL("update sessionlog set status=0 where school_id={kg} and session_id={topic} and uid={uid}")
        .on(
          'kg -> kg.toString,
          'topic -> topicId,
          'uid -> id
        ).executeUpdate()
  }


  def generateMonthQuery(month: Option[String]): String = month match {
    case Some(m) =>
      adaptLocalDB(" and DATE_FORMAT((from_unixtime(update_at / 1000)), '%Y%m')={month} ")
    case None => ""
  }

  def history(kg: Long, topicId: String, from: Option[Long], to: Option[Long], month: Option[String], most: Option[Int]) = DB.withConnection {
    implicit c =>
      Logger.info("h_%s".format(topicId))
      SQL("select * from sessionlog where status=1 and school_id={kg} and session_id={id} " +
        generateMonthQuery(month) + rangerQuery(from, to, most))
        .on(
          'kg -> kg.toString,
          'id -> "h_%s".format(topicId),
          'from -> from,
          'to -> to,
          'month -> month
        ).as(simple(Some("^h_")) *)
  }


  def retrieveSender(kg: Long, conversation: Conversation): Sender = {
    conversation.sender match {
      case Some(phone) if phone.isEmpty =>
        Sender(Parent.phoneSearch(conversation.phone).get.parent_id.get, Some("p"))
      case Some(name) if Employee.phoneExists(conversation.sender_id.getOrElse("0")) =>
        Sender(Employee.show(conversation.sender_id.getOrElse("0")).get.id.get)
      case _ => Sender("3_0_0")
    }
  }

  def generateClassQuery(classes: String): String = classes match {
    case ids if ids.nonEmpty =>
      s" and class_id in (${classes}) "
    case _ => " "
  }


  def lastMessageInClasses(kg: Long, classes: Option[String]) = DB.withConnection {
    implicit c =>
      classes match {
        case Some(ids) =>
          SQL("select *" +
            "            from sessionlog s," +
            "            (" +
            "              select session_id, MAX(s.update_at) last" +
            "              from sessionlog s, childinfo c " +
            "              where 1 = (select 1 from relationmap where child_id=c.child_id and status=1 limit 1) and s.status= 1 " +
            "              and s.school_id= {kg}" +
            "      and session_id= child_id" +
            "      and c.status= 1" +
                  generateClassQuery(ids) +
            "      group by session_id) a" +
            "      where a.last= s.update_at" +
            "      and s.session_id= a.session_id")
            .on(
              'kg -> kg.toString
            ).as(simple() *)
        case None =>
          SQL("select s.* from sessionlog s," +
            "(select session_id, MAX(update_at) last from sessionlog where status=1 and school_id={kg} group by session_id) a, " +
            "childinfo c where 1=(select 1 from relationmap where child_id=c.child_id and status=1 limit 1) and a.last=s.update_at and s.session_id = a.session_id and s.session_id = c.child_id and c.status=1")
            .on(
              'kg -> kg.toString
            ).as(simple() *)
      }

  }

  def splitMedium(urls: String, types: String): Option[List[MediaContent]] = urls match {
    case emptyString if emptyString.isEmpty =>
      Some(List[MediaContent]())
    case multi =>
      val allContent = multi.split(urlSeparator).toList zip (Stream continually types.split(typeSeparator).toList).flatten map {
        (p) => MediaContent(p._1, Some(p._2))
      }
      Some(allContent)
  }

  def simple(prefix: Option[String] = None) = {
    get[String]("session_id") ~
      get[Long]("update_at") ~
      get[Long]("uid") ~
      get[String]("content") ~
      get[String]("media_url") ~
      get[String]("media_type") ~
      get[String]("sender") ~
      get[String]("sender_type") map {
      case topic ~ t ~ id ~ content ~ urls ~ mediaType ~ sender ~ senderType if prefix.isDefined =>
        ChatSession(topic.replaceFirst(prefix.get, ""), Some(t), Some(id), content, None, Sender(sender, Some(senderType)), splitMedium(urls, mediaType))
      case topic ~ t ~ id ~ content ~ url ~ mediaType ~ sender ~ senderType =>
        ChatSession(topic, Some(t), Some(id), content, Some(MediaContent(url, Some(mediaType))), Sender(sender, Some(senderType)))
    }
  }

  val urlSeparator = "  "
  val typeSeparator = ","

  def joinMediumUrls(medium: List[MediaContent]): String = medium.map(_.url).mkString(urlSeparator)

  def joinMediumTypes(medium: List[MediaContent]): String = medium.map(_.`type`.getOrElse("image")).mkString(typeSeparator)

  def update(kg: Long, session: ChatSession, topic: String, id: Long) = DB.withConnection {
    implicit c =>
      val time = System.currentTimeMillis

      val medium = session.medium
      val stmt: String = "update sessionlog set content={content}, media_url={url}, media_type={media_type}, sender={sender}, update_at={update_at}, sender_type={sender_type} " +
        "where uid = {id} and session_id={topic} and school_id={kg}"
      medium match {
        case many if many.nonEmpty =>
          val count = SQL(stmt).on(
            'kg -> kg.toString,
            'id -> id,
            'topic -> s"h_${session.topic}",
            'content -> session.content,
            'url -> joinMediumUrls(many.get),
            'media_type -> joinMediumTypes(many.get),
            'sender -> session.sender.id,
            'sender_type -> session.sender.`type`,
            'update_at -> time
          ).executeUpdate()
          Logger.info(s"update history $count")
          session.copy(timestamp = Some(time))
        case _ =>
          val media = session.media.getOrElse(MediaContent(""))
          val count = SQL(stmt).on(
            'kg -> kg.toString,
            'id -> id,
            'topic -> session.topic,
            'content -> session.content,
            'url -> media.url,
            'media_type -> media.`type`,
            'sender -> session.sender.id,
            'sender_type -> session.sender.`type`,
            'update_at -> time
          ).executeUpdate()
          Logger.info(s"update session $count")
          session.copy(timestamp = Some(time))
      }
  }

  def create(kg: Long, session: ChatSession, originId: String) = DB.withConnection {
    implicit c =>
      val time = session.timestamp.getOrElse(System.currentTimeMillis)
      val medium = session.medium
      val stmt: String = "INSERT INTO sessionlog (school_id, session_id, content, media_url, media_type, sender, update_at, sender_type) VALUES" +
        "({kg}, {id}, {content}, {url}, {media_type}, {sender}, {update_at}, {sender_type})"
      medium match {
        case many if many.nonEmpty =>
          val id = SQL(stmt).on(
            'kg -> kg.toString,
            'id -> session.topic,
            'content -> session.content,
            'url -> joinMediumUrls(many.get),
            'media_type -> joinMediumTypes(many.get),
            'sender -> session.sender.id,
            'sender_type -> session.sender.`type`,
            'update_at -> time
          ).executeInsert()
          ChatSession(originId, Some(time), id, session.content, None, session.sender, many)
        case _ =>
          val media = session.media.getOrElse(MediaContent(""))
          val id = SQL(stmt).on(
            'kg -> kg.toString,
            'id -> session.topic,
            'content -> session.content,
            'url -> media.url,
            'media_type -> media.`type`,
            'sender -> session.sender.id,
            'sender_type -> session.sender.`type`,
            'update_at -> time
          ).executeInsert()
          ChatSession(originId, Some(time), id, session.content, session.media, session.sender)
      }
  }

  def index(kg: Long, sessionId: String, from: Option[Long], to: Option[Long], most: Option[Int] = Some(25)) = DB.withConnection {
    implicit c =>
      SQL("select * from sessionlog where status=1 and school_id={kg} and session_id={id} " +
        rangerQuery(from, to, most))
        .on(
          'kg -> kg.toString,
          'id -> sessionId,
          'from -> from,
          'to -> to
        ).as(simple() *)
  }

  def showHistory(kg: Long, topicId: String, id: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from sessionlog where status=1 and school_id={kg} and session_id={topic} and uid={id}")
        .on(
          'kg -> kg.toString,
          'id -> id,
          'topic -> s"h_$topicId"
        ).as(simple(Some("^h_")) singleOpt)
  }

  def findHistoryById(id: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from sessionlog where uid={id}")
        .on(
          'id -> id
        ).as(simple(Some("^h_")) singleOpt)
  }

}
