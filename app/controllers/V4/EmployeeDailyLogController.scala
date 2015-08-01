package controllers.V4

import controllers.Secured
import models.EmployeeDailyLog
import models.DailyLog.writeEmployeeDailyLog
import play.api.libs.json.Json
import play.api.mvc.Controller

object EmployeeDailyLogController extends Controller with Secured {
  def index(kg: Long, employeeId: String, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(List(EmployeeDailyLog(System.currentTimeMillis(), 1, employeeId, "https://dn-cocobabys.qbox.me/big_shots.jpg", Some("123456")))))
  }
}