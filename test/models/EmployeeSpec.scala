package models

import _root_.helper.TestSupport
import org.specs2.mutable.Specification

class EmployeeSpec extends Specification with TestSupport {

  "Employee" should {
    "be able to login with username and password" in new WithApplication {

      private val user = Employee.authenticate("e0001", "secret").get

      user.login_name must equalTo("e0001")
      user.name must equalTo("王豫")
    }
  }
}
