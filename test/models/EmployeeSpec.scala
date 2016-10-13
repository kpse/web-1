package models

import _root_.helper.TestSupport
import org.specs2.mutable.Specification

class EmployeeSpec extends Specification with TestSupport {
  private val principalPhone: String = "13258249821"
  private val teacherPhone: String = "13708089040"
  private val noClassTeacherPhone: String = "13060003702"
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

    "ignore empty news" in new WithApplication {

      private val created: Employee = Employee.show(teacherPhone).get

      private val underManagement: Boolean = created.managedNews(None)

      underManagement must beFalse
    }

    "manage news as a principal" in new WithApplication {

      private val created: Employee = Employee.show(principalPhone).get

      private val underManagement: Boolean = created.managedNews(Some(News(None, 1, "", "", None, false, None, Some(123), None)))

      underManagement must beTrue
    }

    "manage anything as a principal" in new WithApplication {

      private val created: Employee = Employee.show(principalPhone).get

      private val underManagement: Boolean = created.managedNews(None)

      underManagement must beTrue
    }

    "not manage news without any subordinate classes" in new WithApplication {

      private val created: Employee = Employee.show(noClassTeacherPhone).get

      private val underManagement: Boolean = created.managedNews(Some(News(None, 1, "", "", None, false, None, Some(123), None)))

      underManagement must beFalse
    }

    "manage news which class is out of subordinates classes" in new WithApplication {

      private val created: Employee = Employee.show(teacherPhone).get

      private val underManagement: Boolean = created.managedNews(Some(News(None, 1, "", "", None, false, None, Some(777888), None)))

      underManagement must beTrue
    }

    "not be able to login if school disabled" in new WithApplication {
      Charge.delete(93740362)

      private val failure = Employee.authenticate("e0001", "secret")

      failure must beNone
    }
  }

  def createEmployee(phone: String, loginName: String): Employee = {
    Employee(None, "", phone, 0, "", "", None, "1980-01-01", 93740362, loginName, None, None)
  }

  def createPrincipal(phone: String, loginName: String): Employee = {
    val employee: Employee = createEmployee(phone, loginName)
    employee.copy(privilege_group = Some("principal"))
  }
}
