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
class ClassControllerSpec extends Specification with TestSupport {

  def loggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654680", "token" -> "1386849160798")
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

  "Class" should {
    "check authentication first" in new WithServer {

      val childResponse = route(FakeRequest(GET, "/kindergarten/93740362/class")).get

      status(childResponse) must equalTo(UNAUTHORIZED)

    }

    "not be deleted while children belongs in it" in new WithServer {

      val response = route(loggedRequest(DELETE, "/kindergarten/93740362/class/777888")).get

      status(response) must equalTo(BAD_REQUEST)
    }

    "show all to principal" in new WithServer {

      val response = route(principalLoggedRequest(GET, "/kindergarten/93740362/class")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must equalTo(3)
        case _ => failure
      }
    }

    "show partial to limited privilege teachers" in new WithServer {

      val response = route(twoClassesManagerLoggedRequest(GET, "/kindergarten/93740362/class")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must equalTo(2)
        case _ => failure
      }
    }

    "return empty to no privilege teachers" in new WithServer {

      val response = route(noAccessLoggedRequest(GET, "/kindergarten/93740362/class")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must equalTo(0)
        case _ => failure
      }
    }

  }
}
