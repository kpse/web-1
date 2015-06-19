package controllers

import models.DailyLog
import play.api.cache.Cache
import play.api.libs.json.Json
import play.api.mvc._
import play.api.Play.current

object DailyLogController extends Controller with Secured {

  implicit val write2 = Json.writes[DailyLog]

  def index(kg: Long, childId: String, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn {
    u => _ =>
      val noFutureRecord = to.getOrElse(System.currentTimeMillis)
      val all: List[DailyLog] = Cache.getOrElse[List[DailyLog]](s"allInSchool$kg-child$childId-from$from-to$noFutureRecord-most$most", 30) {
        DailyLog.all(kg, childId, from, Some(noFutureRecord), most)
      }
      Ok(Json.toJson(all))
  }

  def indexInClasses(kg: Long, classIds: String) = IsAuthenticated {
    u => _ =>
      val value: List[DailyLog] = Cache.getOrElse[List[DailyLog]](s"indexInSchool$kg-classes$classIds", 30) {
        DailyLog.lastCheckInClasses(kg, classIds)
      }
      Ok(Json.toJson(value))
  }
}
