package controllers.V4

import controllers.Secured
import controllers.helper.CacheHelper._
import models.V4.SchoolOperationReport
import models.V4.SchoolOperationReport.writeSchoolOperationReport
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc.Controller

object StatisticsController extends Controller with Secured {

  implicit val statisticsControllerCacheKey = "school_monthly_counting"
  createKeyCache(statisticsControllerCacheKey)

  def monthlyCounting(schoolId: Long, lastMonth: DateTime = DateTime.now().minusMonths(1)) = IsOperator {
    u => _ =>
      val month: String = SchoolOperationReport.pattern.print(lastMonth)
      val cacheKey: String = s"monthlyCountFor${schoolId}_In_$month"
      val value: SchoolOperationReport = digFromCache[SchoolOperationReport](cacheKey, 3600 * 24 * 20 , () => {
        SchoolOperationReport.collectTheWholeMonth(schoolId, lastMonth)
      })
      Ok(Json.toJson(value))
  }

  def dailyCounting(schoolId: Long, today: DateTime = DateTime.now().withHourOfDay(1)) = IsOperator {
    u => _ =>
      val date: String = SchoolOperationReport.pattern.print(today)
      val cacheKey: String = s"dailyCountFor${schoolId}_In_$date"
      val value: SchoolOperationReport = digFromCache[SchoolOperationReport](cacheKey, 3600 * 6 , () => {
        SchoolOperationReport.collectTheWholeMonth(schoolId, today)
      })
      Ok(Json.toJson(value))
  }

}
