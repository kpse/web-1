package controllers

import play.api.mvc.Controller
import models.{DailyLogStats, DailyLog, Statistics, ChatSession}
import play.api.libs.json.Json



object StatisticsController extends Controller with Secured {

  implicit val write = Json.writes[Statistics]
  implicit val write1 = Json.writes[DailyLogStats]

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
}
