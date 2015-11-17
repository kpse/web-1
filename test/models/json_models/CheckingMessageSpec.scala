package models.json_models

import helper.TestSupport
import models.DailyLog
import org.specs2.mutable.Specification
import play.Logger

class CheckingMessageSpec extends Specification with TestSupport {

  "CheckChildInfo" should {
    "record daily log after checking in school bus" in new WithApplication {

      private val checkInTypeCode = 11
      private val create: Option[Long] = CheckChildInfo(93740362, "1_1391836223533", checkInTypeCode, 0).create
      create must not beEmpty

      private val all: List[DailyLog] = DailyLog.all(93740362, "1_1391836223533", None, None, None)
      all.takeRight(1).head.notice_type must equalTo(checkInTypeCode)
    }

    "record daily log after checking out school bus" in new WithApplication {

      private val checkOutTypeCode = 12
      private val create: Option[Long] = CheckChildInfo(93740362, "1_1391836223533", checkOutTypeCode, 0).create
      create must not beEmpty

      private val all: List[DailyLog] = DailyLog.all(93740362, "1_1391836223533", None, None, None)
      all.takeRight(1).head.notice_type must equalTo(checkOutTypeCode)
    }

  }

  "CheckInfo" should {
    "record daily log after entering school" in new WithApplication {
      private val checkInSchoolTypeCode = 1
      private val create: Option[Long] = CheckInfo(93740362, "0001234567", 0, checkInSchoolTypeCode, "", 0).create
      create must not beEmpty

      private val all: List[DailyLog] = DailyLog.all(93740362, "1_1391836223533", None, None, None)
      all.takeRight(1).head.notice_type must equalTo(checkInSchoolTypeCode)
    }

    "record daily log after leaving school" in new WithApplication {
      private val checkOutSchoolTypeCode = 0
      private val create: Option[Long] = CheckInfo(93740362, "0001234567", 0, checkOutSchoolTypeCode, "", 0).create
      create must not beEmpty

      private val all: List[DailyLog] = DailyLog.all(93740362, "1_1391836223533", None, None, None)
      all.takeRight(1).head.notice_type must equalTo(checkOutSchoolTypeCode)
    }

    "record daily log after getting off school bus" in new WithApplication {
      private val getOffBusTypeCode = 13
      private val create: Option[Long] = CheckInfo(93740362, "0001234567", 0, getOffBusTypeCode, "", 0).create
      create must not beEmpty

      private val all: List[DailyLog] = DailyLog.all(93740362, "1_1391836223533", None, None, None)
      all.takeRight(1).head.notice_type must equalTo(getOffBusTypeCode)
    }

    "record daily log after getting on school bus" in new WithApplication {
      private val getOnBusTypeCode = 10
      private val create: Option[Long] = CheckInfo(93740362, "0001234567", 0, getOnBusTypeCode, "", 0).create
      create must not beEmpty

      private val all: List[DailyLog] = DailyLog.all(93740362, "1_1391836223533", None, None, None)
      all.takeRight(1).head.notice_type must equalTo(getOnBusTypeCode)
    }

  }
}
