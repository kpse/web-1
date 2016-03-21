package models

import _root_.helper.TestSupport
import org.specs2.mutable.Specification

class SchoolConfigSpec extends Specification with TestSupport {

  "SchoolConfig" should {
    "should apply default value to each config item" in {

      val allDefaults: List[ConfigItem] = SchoolConfig.appendDefaultValue(List())

      allDefaults must contain(ConfigItem("displayVideoMemberDetail", "false"))
      allDefaults must contain(ConfigItem("schoolGroupChat", "true"))
    }

  }
}
