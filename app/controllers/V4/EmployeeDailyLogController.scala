package controllers.V4

import controllers.Secured
import models.{DailyLog, EmployeeDailyLog}
import models.DailyLog.writeEmployeeDailyLog
import play.api.libs.json.Json
import play.api.mvc.Controller

object EmployeeDailyLogController extends Controller with Secured {
  def index(kg: Long, employeeId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(DailyLog.employeeCheck(kg, employeeId, from, to, most)))
  }
}