package controllers

import play.api.mvc.Controller
import models._
import play.api.libs.json.Json
import models.DailyLogStats
import models.Statistics


object StatisticsController extends Controller with Secured {

  implicit val write = Json.writes[Statistics]
  implicit val write1 = Json.writes[DailyLogStats]
  implicit val write2 = Json.writes[AssignmentCount]

  def countSession(schoolId: Long) = IsOperator {
    u => _=>
      Ok(Json.toJson(ChatSession.countInSchool(schoolId)))
  }

  def countGrowingHistory(schoolId: Long) = IsOperator {
    u => _=>
      Ok(Json.toJson(ChatSession.countGrowingHistory(schoolId)))
  }

  def countDailyLogHistory(schoolId: Long) = IsAuthenticated {
    u => _ =>
      Ok(Json.toJson(DailyLog.countHistory(schoolId)))
  }

  def countAssignmentHistory(schoolId: Long, employeeId: Option[String]) = IsAuthenticated {
    u => _ =>
      Ok(Json.toJson(Assignment.countHistory(schoolId, employeeId)))
  }
}
