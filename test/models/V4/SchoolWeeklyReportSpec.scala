package models.V4

import helper.TestSupport
import models.json_models.SchoolIntro
import org.joda.time.DateTime
import org.specs2.mutable.Specification

class SchoolWeeklyReportSpec extends Specification with TestSupport {

  "SchoolWeeklyReport" should {
    "count for all schools" in new WithApplication {

      private val result = SchoolWeeklyReport.weeklyStatistics
      private val allSchools = SchoolIntro.allIds

      result.size must equalTo(allSchools.size)
      allSchools must contain(result.head.school_id)
    }

    "check if stats exists" in new WithApplication {

      private val result = SchoolWeeklyReport.weeklyHistoryData(93740362, DateTime.parse("2016-01-07"))
      result must not beNone

    }

    "check if stats does not exist" in new WithApplication {

      private val result = SchoolWeeklyReport.weeklyHistoryData(93740362, DateTime.parse("2011-01-07"))
      result must beNone

    }

    "do statistics with school, week parameter" in new WithApplication {

      private val result = SchoolWeeklyReport.weeklyCountingLogic(93740362, DateTime.parse("2016-01-07"))
      result.school_id must equalTo(93740362)

    }


  }

}
