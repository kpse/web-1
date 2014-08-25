package controllers

import _root_.helper.TestSupport
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithServer}

@RunWith(classOf[JUnitRunner])
class AssessControllerSpec extends Specification with TestSupport {

  def loggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654680", "token" -> "1386849160798")
  }

  "Access" should {
    "check authentication first" in new WithServer {

      val childResponse = route(FakeRequest(GET, "/kindergarten/93740362/child/123/assess")).get

      status(childResponse) must equalTo(UNAUTHORIZED)

    }

    "list for specific child" in new WithServer {

      val indexResponse = route(loggedRequest(GET, "/kindergarten/93740362/child/1_93740362_374/assess")).get

      status(indexResponse) must equalTo(OK)
      val jsonResponse: JsValue = Json.parse(contentAsString(indexResponse))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must greaterThan(1)
        case _ => failure
      }
    }

  }
}
