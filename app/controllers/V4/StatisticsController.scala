package controllers.V4

import controllers.Secured
import controllers.helper.CacheHelper._
import models.V4.SchoolMonthlyStatistics
import models.V4.SchoolMonthlyStatistics.writeSchoolMonthlyStatistics
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc.Controller

object StatisticsController extends Controller with Secured {

  implicit val statisticsControllerCacheKey = "school_monthly_counting"
  createKeyCache(statisticsControllerCacheKey)

  def monthlyCounting(schoolId: Long, lastMonth: DateTime = DateTime.now().minusMonths(1)) = IsOperator {
    u => _ =>
      val month: String = SchoolMonthlyStatistics.pattern.print(lastMonth)
      val cacheKey: String = s"monthlyCountFor${schoolId}_In_$month"
      val value: SchoolMonthlyStatistics = digFromCache[SchoolMonthlyStatistics](cacheKey, 3600 * 24, () => {
        SchoolMonthlyStatistics.collectTheWholeMonth(schoolId, lastMonth)
      })
      Ok(Json.toJson(value))
  }

}
