package models.V4

import _root_.helper.TestSupport
import org.specs2.mutable.Specification

class AgentSchoolSpec extends Specification with TestSupport {

  "AgentSchool" should {
    "tell if school has been connected with agent" in new WithApplication {

      private val result = AgentSchool(None, 93740362, "学校").connected

      result must beTrue
    }

    "tell if school has not been connected" in new WithApplication {

      private val result = AgentSchool(None, 99999, "学校2").connected

      result must beFalse
    }

  }


}
