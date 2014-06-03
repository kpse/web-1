package controllers

import play.api.mvc.Controller
import models.{Statistics, ChatSession}
import play.api.libs.json.Json



object StatisticsController extends Controller with Secured {

  implicit val write = Json.writes[Statistics]

  def countSession(schoolId: Long) = IsOperator {
    u => _=>
      Ok(Json.toJson(ChatSession.countInSchool(schoolId)))
  }

  def countGrowingHistory(schoolId: Long) = IsOperator {
    u => _=>
      Ok(Json.toJson(ChatSession.countGrowingHistory(schoolId)))
  }
}
