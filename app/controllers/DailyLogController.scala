package controllers

import play.api.mvc._
import models.DailyLog
import play.api.libs.json.Json
import models.json_models.{IOSField, CheckNotification}

object DailyLogController extends Controller with Secured {

  implicit val write2 = Json.writes[DailyLog]

  def index(kg: Long, childId: String, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(DailyLog.all(kg, childId, from, to).take(most.getOrElse(25))))
  }

  def indexInClasses(kg: Long, classIds: String) = IsAuthenticated {
    u=>
      _=>
      Ok(Json.toJson(DailyLog.lastCheckInClasses(kg, classIds)))
  }
}
