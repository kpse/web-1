package controllers.V4

import controllers.Secured
import controllers.helper.CacheHelper._
import models.V4.{SchoolOpertionReport, SchoolOpertionReport$}
import models.V4.SchoolOpertionReport.writeSchoolMonthlyStatistics
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc.Controller

object StatisticsController extends Controller with Secured {

  implicit val statisticsControllerCacheKey = "school_monthly_counting"
  createKeyCache(statisticsControllerCacheKey)

  def monthlyCounting(schoolId: Long, lastMonth: DateTime = DateTime.now().minusMonths(1)) = IsOperator {
    u => _ =>
      val month: String = SchoolOpertionReport.pattern.print(lastMonth)
      val cacheKey: String = s"monthlyCountFor${schoolId}_In_$month"
      val value: SchoolOpertionReport = digFromCache[SchoolOpertionReport](cacheKey, 3600 * 24 * 20 , () => {
        SchoolOpertionReport.collectTheWholeMonth(schoolId, lastMonth)
      })
      Ok(Json.toJson(value))
  }

  def dailyCounting(schoolId: Long, today: DateTime = DateTime.now().withHourOfDay(1)) = IsOperator {
    u => _ =>
      val date: String = SchoolOpertionReport.pattern.print(today)
      val cacheKey: String = s"dailyCountFor${schoolId}_In_$date"
      val value: SchoolOpertionReport = digFromCache[SchoolOpertionReport](cacheKey, 3600 * 6 , () => {
        SchoolOpertionReport.collectTheWholeMonth(schoolId, today)
      })
      Ok(Json.toJson(value))
  }

}
