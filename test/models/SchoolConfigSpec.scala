package models

import _root_.helper.TestSupport
import org.specs2.mutable.Specification

class SchoolConfigSpec extends Specification with TestSupport {

  "SchoolConfig" should {
    "apply default value to each config item" in {

      val allDefaults: List[ConfigItem] = SchoolConfig.appendDefaultValue(List())

      allDefaults must contain(ConfigItem("displayVideoMemberDetail", "false"))
      allDefaults must contain(ConfigItem("schoolGroupChat", "true"))
    }

    "be able to override default value" in {

      val allDefaults: List[ConfigItem] = SchoolConfig.appendDefaultValue(List(ConfigItem("displayVideoMemberDetail", "true")))

      allDefaults must contain(ConfigItem("displayVideoMemberDetail", "true"))
      allDefaults must contain(ConfigItem("schoolGroupChat", "true"))
    }

    "search both global and individual keys for value" in new WithApplication {

      private val value: Option[String] = SchoolConfig.valueOfKey(93740562, "aa")

      value must beNone

      SchoolConfig.addConfig(93740562, ConfigItem("aa", "vv", SchoolConfig.SCHOOL_INDIVIDUAL_SETTING))

      private val value2: Option[String] = SchoolConfig.valueOfKey(93740562, "aa")

      value2 must beSome("vv")
    }

    "be global by default" in new WithApplication {

      SchoolConfig.addConfig(93740562, ConfigItem("this should be global", "guess so", None))

      private val config: SchoolConfig = SchoolConfig.config(93740562)

      config.config must contain(ConfigItem("this should be global", "guess so", SchoolConfig.SUPER_ADMIN_SETTING))
    }

  }
}
