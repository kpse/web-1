package models.V4

import helper.TestSupport
import models.json_models.SchoolIntro
import org.joda.time.DateTime
import org.specs2.mutable.Specification

class SchoolOperationReportSpec extends Specification with TestSupport {
  "SchoolOperationReport" should {

    "count for all schools monthly" in new WithApplication {

      private val result = SchoolOperationReport.monthlyStatistics
      private val allSchools = SchoolIntro.allIds

      result.size must equalTo(allSchools.size)
      allSchools must contain(result.head.school_id)
    }

    "count for all schools daily" in new WithApplication {

      private val result = SchoolOperationReport.dailyStatistics
      private val allSchools = SchoolIntro.allIds

      result.size must equalTo(allSchools.size)
      allSchools must contain(result.head.school_id)
    }

    "check if monthly stats data exists" in new WithApplication {

      private val result = SchoolOperationReport.lastMonthHistoryData(93740362, DateTime.parse("2015-07-01"))
      result must not beNone

    }

    "check if monthly stats data does not exist" in new WithApplication {

      private val result = SchoolOperationReport.lastMonthHistoryData(93740362, DateTime.parse("2016-07-01"))
      result must beNone

    }

    "check if daily stats data exists" in new WithApplication {

      private val result = SchoolOperationReport.specificDayData(93740362, DateTime.parse("2015-07-01"))
      result must not beNone

    }

    "check if daily stats data does not exist" in new WithApplication {

      private val result = SchoolOperationReport.specificDayData(93740362, DateTime.parse("2016-07-01"))
      result must beNone

    }

    "do statistics with school monthly" in new WithApplication {

      private val result = SchoolOperationReport.monthlyCountingLogic(93740362, DateTime.parse("2016-01-07"))
      result.school_id must equalTo(93740362)
      result.month must equalTo("201601")

    }

    "do statistics with school daily" in new WithApplication {

      private val result = SchoolOperationReport.tillTodayCountingLogic(93740362, DateTime.parse("2016-01-07"))
      result.school_id must equalTo(93740362)
      result.day must equalTo("20160107")
    }
  }
}
