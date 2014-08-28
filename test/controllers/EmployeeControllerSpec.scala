package controllers

import _root_.helper.TestSupport
import models.ChildInfo
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.Logger
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithServer}

@RunWith(classOf[JUnitRunner])
class EmployeeControllerSpec extends Specification with TestSupport {
  implicit val writes = Json.writes[ChildInfo]

  def operatorRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_9972", "username" -> "operator")
  }

  def principalLoggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_1122", "username" -> "e0001")
  }

  def twoClassesManagerLoggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_3344", "username" -> "e0002")
  }

  def noAccessLoggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_9977", "username" -> "e0003")
  }

  "Employee" should {
    "check authentication first" in new WithServer {

      val employeeResponse = route(FakeRequest(GET, "/employee")).get

      status(employeeResponse) must equalTo(UNAUTHORIZED)

    }

    "show all children to operator" in new WithServer {

      val response = route(operatorRequest(GET, "/employee")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must greaterThanOrEqualTo(6)
        case _ => failure
      }
    }
  }
}
