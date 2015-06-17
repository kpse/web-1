package models.V2

import anorm.SqlParser._
import anorm._
import models.News
import play.Logger
import play.api.db.DB
import anorm.~
import play.api.Play.current
import play.api.libs.json.Json

object NewsV2 {
  def allIncludeNonPublished(kg: Long, classIds: Option[String], restricted: Boolean, from: Option[Long], to: Option[Long], most: Option[Int]) =
    News.allIncludeNonPublished(kg, classIds, restricted, from, to, most) map tags

  implicit val readNews = Json.reads[News]

  def saveTags(news: News) = DB.withConnection {
    implicit c =>
      SQL("delete from newstags where news_id={newsId}").on('newsId -> news.news_id).execute()
      news.tags map {
        t =>
          SQL("insert into newstags (news_id, tag_id) values ({newsId}, (select uid from tags where name={name}))").on('name -> t, 'newsId -> news.news_id).executeInsert()
      }
  }

  def create(news: News) = DB.withTransaction {
    implicit c =>
      var createdId: Option[Long] = None
      try {
        createdId =
          SQL("insert into news (school_id, title, content, update_at, published, class_id, image, publisher_id, feedback_required) " +
            "values ({kg}, {title}, {content}, {timestamp}, {published}, {class_id}, {image}, {publisher_id}, {feedback_required})")
            .on('content -> news.content,
              'kg -> news.school_id,
              'title -> news.title,
              'publisher_id -> news.publisher_id,
              'timestamp -> System.currentTimeMillis,
              'published -> (if (news.published) 1 else 0),
              'class_id -> news.class_id,
              'image -> news.image,
              'feedback_required -> (if (news.feedback_required.getOrElse(false)) 1 else 0)
            ).executeInsert()
        createdId map (id => saveTags(news.copy(news_id = Some(id))))
        c.commit()
      }
      catch {
        case t: Throwable =>
          Logger.info(s"creating rollback:${t.getMessage}")
          c.rollback()
      }
      Logger.info(s"created news id: $createdId")
      showWithTag(news.school_id, createdId.getOrElse(-1))
  }

  def update(news: News) = DB.withTransaction {
    implicit c =>
      try {
        SQL("update news set content={content}, published={published}, title={title}, " +
          "update_at={timestamp}, class_id={class_id}, image={image}, feedback_required={feedback_required} where uid={id}")
          .on('content -> news.content,
            'title -> news.title,
            'id -> news.news_id,
            'publisher_id -> news.publisher_id,
            'published -> (if (news.published) 1 else 0),
            'timestamp -> System.currentTimeMillis,
            'class_id -> news.class_id,
            'image -> news.image,
            'feedback_required -> (if (news.feedback_required.getOrElse(false)) 1 else 0)
          ).executeUpdate()
        saveTags(news)
        c.commit()
      }
      catch {
        case t: Throwable =>
          Logger.info(s"updating rollback:${t.getMessage}")
          c.rollback()
      }
      Logger.info(s"update news.news_id: ${news.news_id}")
      showWithTag(news.school_id, news.news_id.getOrElse(0))
  }


  val tagsForNews = {
    get[String]("name") ~
      get[String]("desc") map {
      case name ~ desc =>
        name
    }
  }

  def tags(news: News): News = DB.withConnection {
    implicit c =>
      val tags: List[String] = SQL("select * from newstags n, tags t where n.status=1 and n.tag_id = t.uid and news_id={id}").on('id -> news.news_id).as(tagsForNews *)
      news.copy(tags = tags)
  }

  def showWithTag(kg: Long, newsId: Long): Option[News] = News.findById(kg, newsId) map tags

  def delete(id: Long) = DB.withTransaction {
    implicit c =>
      try {
        SQL("update news set status=0 where uid={id}")
          .on(
            'id -> id
          ).execute()
        SQL("update newstags set status=0 where news_id={id}")
          .on(
            'id -> id
          ).execute()
        c.commit()
      }
      catch {
        case t: Throwable =>
          c.rollback()
      }
  }

  def allSortedWithTag(kg: Long, classIds: Option[String], from: Option[Long], to: Option[Long], most: Option[Int]): List[News] =
    News.allSorted(kg, classIds, from, to, most) map tags
}
