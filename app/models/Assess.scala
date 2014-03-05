package models

import play.api.db.DB
import anorm._
import models.helper.RangerHelper._
import anorm.SqlParser._
import anorm.~
import scala.Some
import play.api.Play.current

case class Assess(id: Option[Long], timestamp: Option[Long], publisher: String, comments: String,
                  emotion: Int, dining: Int, rest: Int, activity: Int, game: Int, exercise: Int,
                  self_care: Int, manner: Int)

object Assess {
  val simple = {
    get[Long]("uid") ~
      get[Long]("publish_at") ~
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
      case id ~ t ~ publisher ~ comments ~ emotion ~ dining ~ rest ~ act ~ game ~ exercise ~
        selfCare ~ manner =>
        Assess(Some(id), Some(t), publisher, comments, emotion, dining, rest, act, game, exercise, selfCare, manner)
    }
  }

  def all(kg: Long, childId: String, from: Option[Long], to: Option[Long]) = DB.withConnection {
    implicit c =>
      SQL("select * from assess where school_id={kg} and child_id={child_id} " + rangerQuery(from, to))
        .on(
          'kg -> kg.toString,
          'child_id -> childId,
          'from -> from,
          'to -> to
        ).as(simple *)

  }

}
