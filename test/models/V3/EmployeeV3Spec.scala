package models.V3

import _root_.helper.TestSupport
import models.Employee
import org.specs2.mutable.Specification
import play.Logger

class EmployeeV3Spec extends Specification with TestSupport {

  "Employee" should {
    "know how many ineligible classes are there in the school" in new WithApplication {

      private val employee = EmployeeV3.show(93740362, 111).get

      employee.ineligibleClasses.size must equalTo(4)
      employee.ineligibleClasses must contain[Map[String, Int]](Map("class_id" -> 777888))
      employee.ineligibleClasses must contain[Map[String, Int]](Map("class_id" -> 777666))
      employee.ineligibleClasses must contain[Map[String, Int]](Map("class_id" -> 777999))
      employee.ineligibleClasses must contain[Map[String, Int]](Map("class_id" -> 777667))
    }

    "be eligible for all classes if he/she is a super user" in new WithApplication {

      private val principal = EmployeeV3.show(93740362, 1).get

      principal.ineligibleClasses must beEmpty
    }

    "be eligible for all classes if he/she is a super user" in new WithApplication {

      private val operator = EmployeeV3.show(0, 6).get

      operator.ineligibleClasses must beEmpty
    }

  }

}
