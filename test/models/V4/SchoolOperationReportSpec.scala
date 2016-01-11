package models.V4

import helper.TestSupport
import models.json_models.SchoolIntro
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
  }
}
