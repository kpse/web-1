package models.V2

import _root_.helper.TestSupport
import models.{ConfigItem, SchoolConfig}
import org.specs2.mutable.Specification

class SchoolConfigSpec extends Specification with TestSupport {

  "SchoolConfig" should {
    "search both global and individual keys for value" in new WithApplication {

      private val value: Option[String] = SchoolConfig.valueOfKey(93740562, "aa")

      value must beNone

      SchoolConfig.addConfig(93740562, ConfigItem("aa", "vv", SchoolConfig.SCHOOL_INDIVIDUAL_SETTING))

      private val value2: Option[String] = SchoolConfig.valueOfKey(93740562, "aa")

      value2 must beSome("vv")
    }

  }
}
