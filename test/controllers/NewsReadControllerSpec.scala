package controllers

import _root_.helper.TestSupport
import models.{Parent, News}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.Logger
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class NewsReadControllerSpec extends Specification with TestSupport {
  def loggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654680", "token" -> "1386849160798")
  }

  def adminRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "login_name", "id" -> "3_93740362_1122")
  }

  "News reading" should {
    "check authentication first" in new WithApplication {

      val newsResponse = route(FakeRequest(GET, "/api/v2/kindergarten/93740362/news/6/reader/1234")).get

      status(newsResponse) must equalTo(UNAUTHORIZED)

    }


    "record each read" in new WithApplication {
      private val newReaderId: String = "2_93740362_888"
      val newsReadForParentResponse = route(adminRequest(GET, "/api/v2/kindergarten/93740362/news/6/reader/" + newReaderId)).get

      status(newsReadForParentResponse) must equalTo(NOT_FOUND)

      private val parent = createParentJson(newReaderId)
      val readResponse = route(adminRequest(POST, "/api/v2/kindergarten/93740362/news/6/reader").withBody(parent)).get

      status(readResponse) must equalTo(OK)

      val newsReadForParentResponse2 = route(adminRequest(GET, "/api/v2/kindergarten/93740362/news/6/reader/" + newReaderId)).get

      status(newsReadForParentResponse2) must equalTo(OK)

    }

    "increase read count for news" in new WithApplication {
      private val newReaderId: String = "2_93740362_790"
      val newsReadResponseBefore = route(adminRequest(GET, "/api/v2/kindergarten/93740362/news/6/reader")).get

      status(newsReadResponseBefore) must equalTo(OK)

      val response: JsArray = Json.parse(contentAsString(newsReadResponseBefore)).as[JsArray]
      val oldLength = response.value.length

      private val parent = createParentJson(newReaderId)
      val readResponse = route(adminRequest(POST, "/api/v2/kindergarten/93740362/news/6/reader").withBody(parent)).get

      status(readResponse) must equalTo(OK)

      val newsReadResponseAfter = route(adminRequest(GET, "/api/v2/kindergarten/93740362/news/6/reader")).get

      status(newsReadResponseAfter) must equalTo(OK)
      val response2: JsArray = Json.parse(contentAsString(newsReadResponseAfter)).as[JsArray]
      response2.value.length must equalTo(oldLength + 1)
    }
  }

  def createParentJson(id: String): JsValue = {
    Json.toJson(Parent(Some(id), 93740362, "name", "1234567891", None, 1, "1999-01-02", Some(0), Some(0), Some(1), Some("com")))
  }
}
