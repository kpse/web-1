package models

import _root_.helper.TestSupport
import models.json_models.CheckInfo
import org.specs2.mutable.Specification

class DailyLogSpec extends Specification with TestSupport {

  "DailyLog" should {
    "collect checking records from specific class" in new WithApplication {
      prepareData
      private val index = DailyLog.lastCheckInClasses(93740362L, "777888")

      index.size must equalTo(1)
    }

    "collect checking records from multiple classes" in new WithApplication {
      prepare2Card
      private val index = DailyLog.lastCheckInClasses(93740362L, "777999,777888")

      index.size must equalTo(2)
    }

    "treat empty classId the same as the whole school" in new WithApplication {
      prepare2Card
      private val index = DailyLog.lastCheckInClasses(93740362L, "")
      private val index2 = DailyLog.lastCheckInClasses(93740362L, "777999,777666,777888")

      index.size must greaterThan(0)
      index.size must equalTo(index2.size)
    }

  }

  def prepareData = {
    CheckInfo(93740362L, "0001234567", 0, 1, "", System.currentTimeMillis() - 10000).create
  }

  def prepare2Card = {
    CheckInfo(93740362L, "0001234567", 0, 1, "", System.currentTimeMillis() - 10000).create
    CheckInfo(93740362L, "0001234568", 0, 1, "", System.currentTimeMillis() - 10000).create
  }


}
