package controllers

import play.api.mvc.Controller
import models._
import play.api.libs.json.Json
import models.DailyLogStats
import models.Statistics


object StatisticsController extends Controller with Secured {

  implicit val write = Json.writes[Statistics]
  implicit val write1 = Json.writes[DailyLogStats]
  implicit val write2 = Json.writes[ScoreItem]

  def countSession(schoolId: Long) = IsOperator {
    u => _ =>
      Ok(Json.toJson(ChatSession.countInSchool(schoolId)))
  }

  def countGrowingHistory(schoolId: Long) = IsOperator {
    u => _ =>
      Ok(Json.toJson(ChatSession.countGrowingHistory(schoolId)))
  }

  def countDailyLogHistory(schoolId: Long) = IsAuthenticated {
    u => _ =>
      Ok(Json.toJson(DailyLog.countHistory(schoolId)))
  }

  def countAssignmentHistory(schoolId: Long, employeeId: Option[String]) = IsAuthenticated {
    u => _ =>
      Ok(Json.toJson(ScoreItem.countHistory("assignment")(schoolId, employeeId)))
  }

  def countAssessHistory(schoolId: Long, employeeId: Option[String]) = IsAuthenticated {
    u => _ =>
      Ok(Json.toJson(ScoreItem.countHistory("assess")(schoolId, employeeId)))
  }

  def countConversationHistory(schoolId: Long, employeeId: Option[String]) = IsAuthenticated {
    u => _ =>
      Ok(Json.toJson(ScoreItem.countHistory("sessionlog", "sender")(schoolId, employeeId)))
  }

  def countNewsHistory(schoolId: Long, employeeId: Option[String]) = IsAuthenticated {
    u => _ =>
      Ok(Json.toJson(ScoreItem.countHistory("news")(schoolId, employeeId)))
  }

  def countAllAssignment() = IsOperator {
    u => _ =>
      Ok(Json.toJson(ScoreItem.countAllHistory("assignment")))
  }

  def countAllAssess() = IsOperator {
    u => _ =>
      Ok(Json.toJson(ScoreItem.countAllHistory("assess")))
  }

  def countAllConversation() = IsOperator {
    u => _ =>
      Ok(Json.toJson(ScoreItem.countAllHistory("sessionlog", "sender")))
  }

  def countAllNews() = IsOperator {
    u => _ =>
      Ok(Json.toJson(ScoreItem.countAllHistory("news")))
  }
}
