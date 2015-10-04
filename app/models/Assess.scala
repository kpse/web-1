package models

import play.Logger
import play.api.db.DB
import anorm._
import models.helper.RangerHelper._
import anorm.SqlParser._
import anorm.~
import scala.Some
import play.api.Play.current

case class Assess(id: Option[Long], timestamp: Option[Long], publisher: String, comments: String,
                  emotion: Int, dining: Int, rest: Int, activity: Int, game: Int, exercise: Int,
                  self_care: Int, manner: Int, child_id: String, school_id: Long, publisher_id: Option[String] = None) {

  def exists = DB.withConnection {
    implicit c =>
      SQL("select count(1) from assess where uid={id} and school_id={school_id} and child_id={child_id}")
        .on(
          'id -> id,
          'school_id -> school_id,
          'child_id -> child_id
        ).as(get[Long]("count(1)") single) > 0
  }

}

object Assess {

  def batchCreate(kg: Long, assessments: List[Assess]) = DB.withTransaction {
    implicit c =>
      try {
        val result = assessments map {
          a =>
            createOrUpdate(kg, a.child_id, a)
        }
        c.commit()
        result
      }
      catch {
        case e: Throwable => c.rollback()
          Logger.warn(e.getLocalizedMessage)
          ErrorResponse(e.getLocalizedMessage)
          List[Assess]()
      }
  }

  def delete(kg: Long, childId: String, assessId: Long) = DB.withConnection {
    implicit c =>
      SQL("delete from assess where school_id={kg} and child_id={child_id} and uid = {uid}")
        .on(
          'kg -> kg.toString,
          'child_id -> childId,
          'uid -> assessId
        ).execute
  }


  def findById(id: Option[Long]) = DB.withConnection {
    implicit c =>
      SQL("select * from assess where uid = {uid}").on('uid -> id).as(simple singleOpt)
  }

  def update(assess: Assess, kg: Long, childId: String): Option[Assess] = DB.withConnection {
    implicit c =>
      SQL("update assess set publisher={publisher}, comments={comments}, " +
        "emotion={emotion}, dining={dining}, rest={rest}, activity={activity}, game={game}, " +
        "exercise={exercise}, self_care={self_care}, manner={manner}, publisher_id={publisher_id} where uid={id}")
        .on(
          'publisher -> assess.publisher,
          'publisher_id -> assess.publisher_id,
          'comments -> assess.comments,
          'school_id -> kg.toString,
          'child_id -> childId,
          'emotion -> assess.emotion,
          'dining -> assess.dining,
          'rest -> assess.rest,
          'activity -> assess.activity,
          'game -> assess.game,
          'exercise -> assess.exercise,
          'self_care -> assess.self_care,
          'manner -> assess.manner,
          'id -> assess.id
        ).executeUpdate()
      Some(assess)
  }

  def createOrUpdate(kg: Long, childId: String, assess: Assess): Option[Assess] = {
    assess.id match {
      case Some(id) if assess.exists =>
        update(assess, kg, childId)
      case _ =>
        val inserted: Option[Long] = create(assess, kg, childId)
        findById(inserted)
    }


  }


  def create(assess: Assess, kg: Long, childId: String): Option[Long] = DB.withConnection {
    implicit c =>
      SQL("insert into assess (school_id, child_id, update_at, publisher, comments, " +
        "emotion, dining, rest, activity, game, exercise, self_care, manner, publisher_id) " +
        "values ({school_id}, {child_id}, {update_at}, {publisher}, {comments}, {emotion}, {dining}, " +
        "{rest}, {activity}, {game}, {exercise}, {self_care}, {manner}, {publisher_id})")
        .on(
          'update_at -> System.currentTimeMillis,
          'publisher -> assess.publisher,
          'publisher_id -> assess.publisher_id,
          'comments -> assess.comments,
          'school_id -> kg.toString,
          'child_id -> childId,
          'emotion -> assess.emotion,
          'dining -> assess.dining,
          'rest -> assess.rest,
          'activity -> assess.activity,
          'game -> assess.game,
          'exercise -> assess.exercise,
          'self_care -> assess.self_care,
          'manner -> assess.manner
        ).executeInsert()
  }

  val simple = {
    get[Long]("uid") ~
      get[String]("child_id") ~
      get[String]("school_id") ~
      get[Long]("update_at") ~
      get[String]("publisher") ~
      get[String]("comments") ~
      get[Int]("emotion") ~
      get[Int]("dining") ~
      get[Int]("rest") ~
      get[Int]("activity") ~
      get[Int]("game") ~
      get[Int]("exercise") ~
      get[Int]("self_care") ~
      get[Int]("manner") map {
      case id ~ childId ~ schoolId ~ t ~ publisher ~ comments ~ emotion ~ dining ~ rest ~ act ~ game ~ exercise ~
        selfCare ~ manner =>
        Assess(Some(id), Some(t), publisher, comments, emotion, dining, rest, act, game, exercise, selfCare, manner, childId, schoolId.toLong)
    }
  }

  def all(kg: Long, childId: String, from: Option[Long], to: Option[Long], most: Option[Int] = Some(25)) = DB.withConnection {
    implicit c =>
      SQL(s"select * from assess where school_id={kg} and child_id={child_id} ${rangerQuery(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'child_id -> childId,
          'from -> from,
          'to -> to
        ).as(simple *)

  }

}
