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

    "have default password as phone number" in new WithApplication {

      Employee.create(createEmployee("11223344556", "newcreated"))

      private val user = Employee.authenticate("newcreated", "11223344556").get
      user.phone must equalTo("11223344556")
    }
  }

  def createEmployee(phone: String, loginName: String): Employee = {
    Employee(None, "", phone, 0, "", "", None, "1980-01-01", 123, loginName, None, None)
  }
}
