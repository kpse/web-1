package models.V3

import _root_.helper.TestSupport
import models.V4.AgentSchool
import org.specs2.mutable.Specification
import play.Logger

class SchoolClassV3Spec extends Specification with TestSupport {

  "SchoolClass" should {
    "be created with managers" in new WithApplication {

      private val result = SchoolClassV3(None, 93740362, None, "某班", Some(List(111))).create
      Logger.info(result.toString)
      private val created: SchoolClassV3 = SchoolClassV3.show(93740362, result.get.id.get).get
      created.managers must beSome(List(111))
    }

    "be updated with managers" in new WithApplication {

      private val result = SchoolClassV3(None, 93740362, None, "某班", Some(List(111, 112))).create
      Logger.info(result.toString)
      private val created: SchoolClassV3 = SchoolClassV3.show(93740362, result.get.id.get).get
      created.managers must beSome(List(111, 112))
    }

  }


}
