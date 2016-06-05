package controllers.V8

import helper.TestSupport
import models.V5.{Invitation, InvitationCode, InvitationPhoneKey, NewParent}
import models.V8.IMKeyword
import models.{Parent, Verification}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.cache.Cache

@RunWith(classOf[JUnitRunner])
class IMKeywordControllerSpec extends Specification with TestSupport {
  def loggedParent(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654680", "token" -> "1386849160798")
  }

  "IMKeywordController" should {
    "check authentication first" in new WithApplication {
      val response = route(FakeRequest(GET, "/api/v8/kindergarten/93740362/im_keyword")).get

      status(response) must equalTo(UNAUTHORIZED)
    }

    "check authentication first for post" in new WithApplication {
      val response = route(FakeRequest(POST, "/api/v8/kindergarten/93740362/im_keyword").withBody(Json.toJson(IMKeyword(None, "new word")))).get

      status(response) must equalTo(UNAUTHORIZED)
    }

    "not allow keywords duplication" in new WithApplication {
      val keywordsRes = route(loggedParent(GET, "/api/v8/kindergarten/93740362/im_keyword")).get

      status(keywordsRes) must equalTo(OK)
      contentType(keywordsRes) must beSome.which(_ == "application/json")
      val response: JsArray = Json.parse(contentAsString(keywordsRes)).as[JsArray]

      response.value.size must equalTo(3)
      private val existingWord: String = (response.value.head \ "word").as[String]

      val reCreateExistingWord = route(loggedParent(POST, "/api/v8/kindergarten/93740362/im_keyword").withBody(Json.toJson(IMKeyword(None, existingWord)))).get

      status(reCreateExistingWord) must equalTo(OK)
      contentType(reCreateExistingWord) must beSome.which(_ == "application/json")

      val afterCreating = route(loggedParent(GET, "/api/v8/kindergarten/93740362/im_keyword")).get

      status(afterCreating) must equalTo(OK)
      val response2: JsArray = Json.parse(contentAsString(afterCreating)).as[JsArray]

      response2.value.size must equalTo(3)

    }
  }
}
