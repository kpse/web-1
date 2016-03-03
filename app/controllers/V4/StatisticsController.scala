package controllers.V4

import controllers.Secured
import models.V4.SchoolWeeklyReport._
import models.V4.SchoolOperationReport
import models.V4.SchoolOperationReport._
import models.json_models.SchoolIntro
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import play.api.libs.json.Json
import play.api.mvc.Controller

object StatisticsController extends Controller with Secured {

  def monthlyCounting(schoolId: Long, lastMonth: DateTime = DateTime.now().minusMonths(1)) = IsOperator {
    u => _ =>
      val value: SchoolOperationReport = monthlyCountingLogic(schoolId, lastMonth)
      Ok(Json.toJson(value))
  }

  def dailyCounting(schoolId: Long, today: DateTime = DateTime.now().withHourOfDay(1)) = IsOperator {
    u => _ =>
      val value: SchoolOperationReport = tillTodayCountingLogic(schoolId, today)
      Ok(Json.toJson(value))
  }

  val pattern: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMM")

  def allSchoolsMonthlyCounting(lastMonth: Option[String]) = IsOperator {
    u => _ =>
      val specificMonth: DateTime = lastMonth match {
        case Some(day) => pattern.parseDateTime(day)
        case None => DateTime.now().minusMonths(1)
      }

      val allData = SchoolIntro.allIds map (monthlyCountingLogic(_, specificMonth))
      Ok(Json.toJson(allData))
  }

  def allSchoolsDailyCounting(today: DateTime = DateTime.now().withHourOfDay(1)) = IsOperator {
    u => _ =>
      val allData = SchoolIntro.allIds map (tillTodayCountingLogic(_, today))
      Ok(Json.toJson(allData))
  }

  def allSchoolsWeeklyCounting(today: DateTime = DateTime.now().minusWeeks(1).withDayOfWeek(1)) = IsOperator {
    u => _ =>
      val allData = SchoolIntro.allIds map (weeklyCountingLogic(_, today))
      Ok(Json.toJson(allData))
  }

  def allMonths(schoolId: Long) = IsOperator {
    u => _ =>
      val allData = SchoolOperationReport.schoolStats(schoolId)
      Ok(Json.toJson(allData))
  }

}
