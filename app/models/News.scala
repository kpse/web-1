package models

import anorm.SqlParser._
import anorm.{~, _}
import models.helper.RangerHelper.rangerQuery
import play.api.Play.current
import play.api.db.DB

case class News(news_id: Option[Long], school_id: Long, title: String, content: String, timestamp: Option[Long],
                published: Boolean, notice_type: Option[Int], class_id: Option[Int], image: Option[String],
                publisher_id: Option[String] = None, feedback_required: Option[Boolean] = Some(false), tags: List[String] = List(),
                images: Option[List[String]] = None, sms: Option[String] = None) {
  def toPreview: Option[NewsPreview] = news_id.map(NewsPreview)
}

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

  def create(form: (Long, String, String, Option[Boolean], Option[Int], Option[String], Option[String], Option[Boolean], Option[List[String]], Option[String])) = DB.withConnection {
    implicit c =>
      val createdId: Option[Long] =
        SQL("insert into news (school_id, title, content, update_at, published, class_id, image, images, publisher_id, feedback_required) " +
          "values ({kg}, {title}, {content}, {timestamp}, {published}, {class_id}, {image}, {images}, {publisher_id}, {feedback_required})")
          .on('content -> form._3,
            'kg -> form._1.toString,
            'title -> form._2,
            'publisher_id -> form._7,
            'timestamp -> System.currentTimeMillis,
            'published -> (if (form._4.getOrElse(false)) 1 else 0),
            'class_id -> form._5.getOrElse(0),
            'image -> form._6.getOrElse(""),
            'feedback_required -> (if (form._8.getOrElse(false)) 1 else 0),
            'images -> form._9.getOrElse(List()).mkString(imagesSplitter),
            'sms -> form._10
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

  def update(form: (Long, Long, String, String, Boolean, Option[Int], Option[String], Option[String], Option[Boolean], Option[List[String]], Option[String]), kg: Long) = DB.withConnection {
    implicit c =>
      SQL("update news set content={content}, published={published}, title={title}, " +
        "update_at={timestamp}, class_id={class_id}, image={image}, images={images}, feedback_required={feedback_required} where uid={id}")
        .on('content -> form._4,
          'title -> form._3,
          'id -> form._1,
          'publisher_id -> form._8,
          'published -> (if (form._5) 1 else 0),
          'timestamp -> System.currentTimeMillis,
          'class_id -> form._6,
          'image -> form._7,
          'feedback_required -> (if (form._9.getOrElse(false)) 1 else 0),
          'images -> form._10.getOrElse(List()).mkString(imagesSplitter),
          'sms -> form._11
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


  val NOTICE_TYPE_SCHOOL_INFO = Some(2)

  val simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[String]("title") ~
      get[String]("content") ~
      get[Long]("update_at") ~
      get[Option[Int]]("class_id") ~
      get[Int]("published") ~
      get[Option[String]]("publisher_id") ~
      get[Option[Int]]("feedback_required") ~
      get[Option[String]]("images") ~
      get[Option[String]]("sms") ~
      get[Option[String]]("image") map {
      case id ~ school_id ~ title ~ content ~ timestamp ~ classId ~ 1 ~ publisher_id ~ feedback ~ images ~ sms ~ image =>
        News(Some(id), school_id.toLong, title, content, Some(timestamp), true, NOTICE_TYPE_SCHOOL_INFO, classId, Some(image.getOrElse("")), publisher_id, Some(feedback == Some(1)), List(), splitMultiple(images), sms)
      case id ~ school_id ~ title ~ content ~ timestamp ~ classId ~ 0 ~ publisher_id ~ feedback ~ images ~ sms ~ image =>
        News(Some(id), school_id.toLong, title, content, Some(timestamp), false, NOTICE_TYPE_SCHOOL_INFO, classId, Some(image.getOrElse("")), publisher_id, Some(feedback == Some(1)), List(), splitMultiple(images), sms)
    }
  }

  val imagesSplitter = ","
  def splitMultiple(input: Option[String]): Option[List[String]] = input match {
    case Some(urls) if urls.nonEmpty => Some(urls.split(imagesSplitter).toList)
    case _ => None
  }

  val allNewsSql = "select * from news where school_id={kg} and published=1 and status=1 "

  def all(kg: Long): List[News] = DB.withConnection {
    implicit c =>
      SQL(allNewsSql + " order by update_at DESC")
        .on('kg -> kg.toString)
        .as(simple *)
  }

  def allSorted(kg: Long, classId: Option[String], from: Option[Long], to: Option[Long], most: Option[Int]): List[News] = DB.withConnection {
    implicit c =>
      classId match {
        case Some(ids) =>
          SQL(allNewsSql + " and class_id in (%s, 0) ".format(ids) + rangerQuery(from, to, most))
            .on(
              'kg -> kg.toString,
              'from -> from,
              'to -> to
            ).as(simple *)
        case None =>
          SQL(allNewsSql + " and class_id=0 " + rangerQuery(from, to, most))
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

  def allIncludeNonPublished(kg: Long, classId: Option[String], restrict: Boolean, from: Option[Long], to: Option[Long], most: Option[Int]): List[News] = DB.withConnection {
    implicit c =>
      SQL("select * from news where school_id={kg} and status=1 " + generateClassCondition(classId, restrict) + " " + rangerQuery(from, to, most))
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple *)
  }


}