package controllers

import _root_.helper.TestSupport
import models.{Assess, VideoMember}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{JsResult, JsArray, JsValue, Json}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithServer}

@RunWith(classOf[JUnitRunner])
class AssessControllerSpec extends Specification with TestSupport {

  implicit val read = Json.reads[Assess]
  implicit val write = Json.writes[Assess]

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

    "be created by teacher" in new WithApplication {

      val oldResponse = route(loggedRequest(GET, "/kindergarten/93740362/child/1_93740362_374/assess")).get
      val oldValue: JsValue = Json.parse(contentAsString(oldResponse))
      private val result: JsResult[List[Assess]] = oldValue.validate[List[Assess]]
      private val before: Int = result.asOpt.get.size

      private val json: JsValue = Json.toJson(Assess(None, None, "a", "comments", 3, 3, 3,3, 3, 3, 3, 3, "1_93740362_374", 93740362, None))
      val createResponse = route(loggedRequest(POST, "/kindergarten/93740362/child/1_93740362_374/assess").withBody(json)).get

      status(createResponse) must equalTo(OK)

      val response = route(loggedRequest(GET, "/kindergarten/93740362/child/1_93740362_374/assess")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must equalTo(before + 1)
        case _ => failure
      }
    }

  }
}
