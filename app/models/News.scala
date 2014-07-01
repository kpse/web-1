package models

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import anorm.~
import play.api.Play.current
import models.helper.RangerHelper.rangerQuery

case class News(news_id: Long, school_id: Long, title: String, content: String, timestamp: Long, published: Boolean, notice_type: Int, class_id: Option[Int], image: Option[String], publisher_id: Option[String]=None)

case class NewsPreview(id: Long)

object News {
  val preview = {
    get[Long]("uid") map {
      case id =>
        NewsPreview(id)
    }
  }

  def previewAllIncludeNonPublished(kg: Long, classId: Option[String], restrict: Boolean) = DB.withConnection {
    implicit c =>
      SQL("select uid from news where school_id={kg} and status=1 " + generateClassCondition(classId, restrict))
        .on(
          'kg -> kg.toString
        ).as(preview *)

  }

  def create(form: (Long, String, String, Option[Boolean], Option[Int], Option[String], Option[String])) = DB.withConnection {
    implicit c =>
      val createdId: Option[Long] =
        SQL("insert into news (school_id, title, content, update_at, published, class_id, image, publisher_id) " +
          "values ({kg}, {title}, {content}, {timestamp}, {published}, {class_id}, {image}, {publisher_id})")
          .on('content -> form._3,
            'kg -> form._1.toString,
            'title -> form._2,
            'publisher_id -> form._7,
            'timestamp -> System.currentTimeMillis,
            'published -> (if (form._4.getOrElse(false)) 1 else 0),
            'class_id -> form._5.getOrElse(0),
            'image -> form._6.getOrElse("")
          ).executeInsert()
      findById(form._1, createdId.getOrElse(-1))
  }

  def delete(id: Long) = DB.withConnection {
    implicit c =>
      SQL("update news set status=0 where uid={id}")
        .on(
          'id -> id
        ).execute()
  }

  def update(form: (Long, Long, String, String, Boolean, Option[Int], Option[String], Option[String]), kg: Long) = DB.withConnection {
    implicit c =>
      SQL("update news set content={content}, published={published}, title={title}, " +
        "update_at={timestamp}, class_id={class_id}, image={image} where uid={id}")
        .on('content -> form._4,
          'title -> form._3,
          'id -> form._1,
          'publisher_id -> form._8,
          'published -> (if (form._5) 1 else 0),
          'timestamp -> System.currentTimeMillis,
          'class_id -> form._6,
          'image -> form._7
        ).executeUpdate()
      findById(kg, form._1)
  }

  def findById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from news where school_id={kg} and uid={id}")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple.singleOpt)
  }


  val NOTICE_TYPE_SCHOOL_INFO = 2

  val simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[String]("title") ~
      get[String]("content") ~
      get[Long]("update_at") ~
      get[Option[Int]]("class_id") ~
      get[Int]("published") ~
      get[Option[String]]("image") map {
      case id ~ school_id ~ title ~ content ~ timestamp ~ classId ~ 1 ~ image =>
        News(id, school_id.toLong, title, content, timestamp, true, NOTICE_TYPE_SCHOOL_INFO, classId, Some(image.getOrElse("")))
      case id ~ school_id ~ title ~ content ~ timestamp ~ classId ~ 0 ~ image =>
        News(id, school_id.toLong, title, content, timestamp, false, NOTICE_TYPE_SCHOOL_INFO, classId, Some(image.getOrElse("")))
    }
  }

  val allNewsSql = "select * from news where school_id={kg} and published=1 and status=1 "

  def all(kg: Long): List[News] = DB.withConnection {
    implicit c =>
      SQL(allNewsSql + " order by update_at DESC")
        .on('kg -> kg.toString)
        .as(simple *)
  }

  def allSorted(kg: Long, classId: Option[String], from: Option[Long], to: Option[Long]): List[News] = DB.withConnection {
    implicit c =>
      classId match {
        case Some(ids) =>
          SQL(allNewsSql + " and class_id in (%s, 0) ".format(ids) + rangerQuery(from, to))
            .on(
              'kg -> kg.toString,
              'from -> from,
              'to -> to
            ).as(simple *)
        case None =>
          SQL(allNewsSql + " and class_id=0 " + rangerQuery(from, to))
            .on(
              'kg -> kg.toString,
              'from -> from,
              'to -> to
            )
            .as(simple *)
      }
  }

  def generateClassCondition(classId: Option[String], restrict: Boolean): String = {
    classId match {
      case Some(ids) if restrict =>
        " and class_id in (%s) ".format(ids)
      case Some(ids) =>
        " and class_id in (%s, 0) ".format(ids)
      case None =>
        ""
    }
  }

  def allIncludeNonPublished(kg: Long, classId: Option[String], restrict: Boolean, from: Option[Long], to: Option[Long]): List[News] = DB.withConnection {
    implicit c =>
      SQL("select * from news where school_id={kg} and status=1 " + generateClassCondition(classId, restrict) + " " + rangerQuery(from, to))
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple *)
  }


}