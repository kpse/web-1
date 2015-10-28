package models

import _root_.helper.TestSupport
import models.json_models.{CheckChildInfo, CheckInfo}
import org.specs2.mutable.Specification
import play.Logger

class CheckInfoSpec extends Specification with TestSupport {

  "CheckInfo" should {
    "be able to convert to push message" in new WithApplication {
      private val channelIdForPush = "13"
      private val singleParentCard: String = "0001234568"
      private val info: CheckInfo = CheckInfo(93740362L, singleParentCard, 0, 1, "", 0)
      private val notifications = info.toNotifications

      notifications.size must equalTo(1)
      notifications.head.channelid must equalTo(channelIdForPush)
    }

    "remove channel id duplicated push messages" in new WithApplication {
      private val channelIdForPush = "123"
      private val dualParentsCard: String = "0001234580"
      private val info: CheckInfo = CheckInfo(93740362L, dualParentsCard, 0, 1, "", 0)
      private val notifications = info.toNotifications

      notifications.size must equalTo(1)
      notifications.head.channelid must equalTo(channelIdForPush)
    }

    "keep different channel id in push messages" in new WithApplication {

      private val dualParentsCard: String = "0091234567"
      private val info: CheckInfo = CheckInfo(93740362L, dualParentsCard, 0, 1, "", 0)
      private val notifications = info.toNotifications

      notifications.size must equalTo(2)
      notifications.map(_.channelid) must contain("3")
      notifications.map(_.channelid) must contain("7")

    }


  }

  "CheckChildInfo" should {
    "be able to convert to push message" in new WithApplication {
      private val channelIdForPush = "13"
      private val singleParentChild: String = "1_93740362_456"
      private val info: CheckChildInfo = CheckChildInfo(93740362L, singleParentChild, 0, 0)
      private val notifications = info.toNotifications

      notifications.size must equalTo(1)
      notifications.head.channelid must equalTo(channelIdForPush)
    }

    "remove channel id duplicated push messages" in new WithApplication {
      private val channelIdForPush = "123"
      private val dualParentsChild: String = "1_93740362_374"
      private val info: CheckChildInfo = CheckChildInfo(93740362L, dualParentsChild, 0, 0)
      private val notifications = info.toNotifications

      notifications.size must equalTo(1)
      notifications.head.channelid must equalTo(channelIdForPush)
    }

    "keep different channel id in push messages" in new WithApplication {

      private val dualParentsChild: String = "1_1391836223533"
      private val info: CheckChildInfo = CheckChildInfo(93740362L, dualParentsChild, 0, 0)
      private val notifications = info.toNotifications

      notifications.size must equalTo(2)
      notifications.map(_.channelid) must contain("3")
      notifications.map(_.channelid) must contain("7")

    }


  }

}
