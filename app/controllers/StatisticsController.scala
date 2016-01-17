package controllers

import play.api.cache.Cache
import play.api.mvc.Controller
import models._
import play.api.libs.json.Json
import models.DailyLogStats
import models.Statistics
import play.api.Play.current


object StatisticsController extends Controller with Secured {

  implicit val write = Json.writes[Statistics]
  implicit val write1 = Json.writes[DailyLogStats]
  implicit val write2 = Json.writes[ScoreItem]

  def countSession(schoolId: Long) = IsOperator {
    u => _ =>
      val value: Statistics = Cache.getOrElse[Statistics](s"sessionCountIn$schoolId", 3600 * 24) {
        ChatSession.countInSchool(schoolId)
      }
      Ok(Json.toJson(value))
  }

  def countGrowingHistory(schoolId: Long) = IsOperator {
    u => _ =>
      val value: Statistics = Cache.getOrElse[Statistics](s"growingIn$schoolId", 3600 * 24) {
        ChatSession.countGrowingHistory(schoolId)
      }
      Ok(Json.toJson(value))
  }

  def countDailyLogHistory(schoolId: Long) = IsAuthenticated {
    u => _ =>
      val value: List[DailyLogStats] = Cache.getOrElse[List[DailyLogStats]](s"dailyLogCountIn$schoolId", 3600 * 24) {
        DailyLog.countHistory(schoolId)
      }
      Ok(Json.toJson(value))
  }

  def countAssessHistory(schoolId: Long, employeeId: Option[String]) = IsAuthenticated {
    u => _ =>
      val value: List[ScoreItem] = Cache.getOrElse[List[ScoreItem]](s"assessCountIn$schoolId-employee$employeeId", 3600 * 24) {
        ScoreItem.countHistory("assess")(schoolId, employeeId)
      }
      Ok(Json.toJson(value))
  }

  def countConversationHistory(schoolId: Long, employeeId: Option[String]) = IsAuthenticated {
    u => _ =>
      val value: List[ScoreItem] = Cache.getOrElse[List[ScoreItem]](s"sessionlogCountIn$schoolId-employee$employeeId", 3600 * 24) {
        ScoreItem.countHistory("sessionlog", "sender")(schoolId, employeeId)
      }
      Ok(Json.toJson(value))
  }

  def countNewsHistory(schoolId: Long, employeeId: Option[String]) = IsAuthenticated {
    u => _ =>
      val value: List[ScoreItem] = Cache.getOrElse[List[ScoreItem]](s"newsCountIn$schoolId-employee$employeeId", 3600 * 24) {
        ScoreItem.countHistory("news")(schoolId, employeeId)
      }
      Ok(Json.toJson(value))
  }

  def countAllAssess() = IsOperator {
    u => _ =>
      val value: List[ScoreItem] = Cache.getOrElse[List[ScoreItem]]("assessCount", 3600 * 24) {
        ScoreItem.countAllHistory("assess")
      }
      Ok(Json.toJson(value))
  }

  def countAllConversation() = IsOperator {
    u => _ =>
      val value: List[ScoreItem] = Cache.getOrElse[List[ScoreItem]]("sessionlogCount", 3600 * 24) {
        ScoreItem.countAllHistory("sessionlog", "sender")
      }
      Ok(Json.toJson(value))
  }

  def countAllNews() = IsOperator {
    u => _ =>
      val value: List[ScoreItem] = Cache.getOrElse[List[ScoreItem]]("newsCount", 3600 * 24) {
        ScoreItem.countAllHistory("news")
      }
      Ok(Json.toJson(value))
  }

  @deprecated(since = "2016-01-17")
  def countAllAssignment() = IsOperator {
    u => _ =>
      val value: List[ScoreItem] = Cache.getOrElse[List[ScoreItem]]("assignmentCount", 3600 * 24) {
        ScoreItem.countAllHistory("assignment")
      }
      Ok(Json.toJson(value))
  }

  @deprecated(since = "2016-01-17")
  def countAssignmentHistory(schoolId: Long, employeeId: Option[String]) = IsAuthenticated {
    u => _ =>
      val value: List[ScoreItem] = Cache.getOrElse[List[ScoreItem]](s"assignmentCountIn$schoolId-employee$employeeId", 3600 * 24) {
        ScoreItem.countHistory("assignment")(schoolId, employeeId)
      }
      Ok(Json.toJson(value))
  }
}
