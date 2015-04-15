package controllers.V2

import _root_.helper.TestSupport
import models.News
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.Logger
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class RemovedListControllerV2Spec extends Specification with TestSupport {
  def loggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654680", "token" -> "1386849160798")
  }

  def principalRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "login_name", "id" -> "3_93740362_1122")
  }

  "RemovedList" should {
    "check authentication first" in new WithApplication {

      val response = route(FakeRequest(GET, "/api/v2/kindergarten/93740362/removed_children")).get

      status(response) must equalTo(UNAUTHORIZED)

      val response2 = route(loggedRequest(GET, "/api/v2/kindergarten/93740362/removed_children")).get

      status(response2) must equalTo(UNAUTHORIZED)

    }

    "list deleted children for principals" in new WithApplication {

      val deleted = route(principalRequest(DELETE, "/kindergarten/93740362/child/1_1391836223533")).get

      status(deleted) must equalTo(OK)

      val response = route(principalRequest(GET, "/api/v2/kindergarten/93740362/removed_children")).get

      status(response) must equalTo(OK)

      private val array: JsArray = Json.parse(contentAsString(response)).as[JsArray]
      (array(0) \ "child_id").as[String] must equalTo("1_1391836223533")
    }

    "list deleted parents for principals" in new WithApplication {

      val deleted = route(principalRequest(DELETE, "/kindergarten/93740362/parent/13402815317")).get

      status(deleted) must equalTo(OK)


      val response = route(principalRequest(GET, "/api/v2/kindergarten/93740362/removed_parents")).get

      status(response) must equalTo(OK)

      private val array: JsArray = Json.parse(contentAsString(response)).as[JsArray]
      (array(0) \ "parent_id").as[String] must equalTo("2_93740362_123")

    }

    "list deleted employees for principals" in new WithApplication {

      val deleted = route(principalRequest(DELETE, "/kindergarten/93740362/employee/23258249821")).get

      status(deleted) must equalTo(OK)

      val response = route(principalRequest(GET, "/api/v2/kindergarten/93740362/removed_employees")).get

      status(response) must equalTo(OK)

      private val array: JsArray = Json.parse(contentAsString(response)).as[JsArray]
      (array(0) \ "id").as[String] must equalTo("3_93740362_1022")

    }

    "list deleted classes for principals" in new WithApplication {

      val deleted = route(principalRequest(DELETE, "/kindergarten/93740362/class/777667")).get

      status(deleted) must equalTo(OK)

      val response = route(principalRequest(GET, "/api/v2/kindergarten/93740362/removed_classes")).get

      status(response) must equalTo(OK)

      private val array: JsArray = Json.parse(contentAsString(response)).as[JsArray]
      (array(0) \ "class_id").as[Int] must equalTo(777667)

    }
  }
}
