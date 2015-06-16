package controllers

import _root_.helper.TestSupport
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import play.api.test.FakeRequest
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.test.Helpers._
import models.{News, ParentPhoneCheck}
import play.api.data.Form
import play.api.data.Forms._
import scala.text
import play.api.libs.json.JsArray
import play.Logger

@RunWith(classOf[JUnitRunner])
class NewsControllerSpec extends Specification with TestSupport {
  def loggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654680", "token" -> "1386849160798")
  }

  def adminRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "login_name", "id" -> "3_93740362_1122")
  }

  "News" should {
    "check authentication first" in new WithApplication {

      val newsResponse = route(FakeRequest(GET, "/kindergarten/93740362/news")).get

      status(newsResponse) must equalTo(UNAUTHORIZED)

    }

    "report less than 25 pieces by default" in new WithApplication {

      val newsResponse = route(loggedRequest(GET, "/kindergarten/93740362/news")).get

      status(newsResponse) must equalTo(OK)
      contentType(newsResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(newsResponse))
      response match {
        case JsArray(arr) =>
          arr.length must be lessThan 25
          (arr(0) \ "news_id").as[Long] must equalTo(6L)
        case _ => failure
      }
    }

    "accept parameter from" in new WithApplication {

      val newsResponse = route(loggedRequest(GET, "/kindergarten/93740362/news?from=5")).get

      status(newsResponse) must equalTo(OK)
      contentType(newsResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(newsResponse))
      response match {
        case JsArray(arr) =>
          arr.length must equalTo(1)
          (arr(0) \ "news_id").as[Long] must equalTo(6L)
        case _ => failure
      }

    }

    "accept parameter to" in new WithApplication {

      val newsResponse = route(loggedRequest(GET, "/kindergarten/93740362/news?to=5")).get

      status(newsResponse) must equalTo(OK)
      contentType(newsResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(newsResponse))
      response match {
        case JsArray(arr) =>
          arr.length must greaterThan(1)
          (arr(0) \ "news_id").as[Long] must equalTo(5L)
        case _ => failure
      }

    }

    "accept parameter most" in new WithApplication {

      val newsResponse = route(loggedRequest(GET, "/kindergarten/93740362/news?most=2")).get

      status(newsResponse) must equalTo(OK)
      contentType(newsResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(newsResponse))
      response match {
        case JsArray(arr) =>
          arr.length must equalTo(2)
          (arr(0) \ "news_id").as[Long] must equalTo(6L)
        case _ => failure
      }

    }
    implicit val writes = Json.writes[News]
    "check publisher while creating news" in new WithApplication {
      val wrongId = "123"
      val requestBody =
        Json.toJson(News(Some(999),
          93740362,
          "title",
          "content", None,
          false, None,
          Some(777666),
          None,
          Some("123")))
      val newsResponse = route(adminRequest(POST, "/kindergarten/93740362/admin/" + wrongId + "/news").withJsonBody(requestBody)).get

      status(newsResponse) must equalTo(FORBIDDEN)

    }

    "check publisher while updating news" in new WithApplication {
      val wrongId = "123"
      val requestBody =
        Json.toJson(News(Some(1),
          93740362,
          "title",
          "content", None,
          false, None,
          Some(777666),
          None,
          Some("123")))
      val newsResponse = route(adminRequest(POST, "/kindergarten/93740362/admin/" + wrongId + "/news/1").withJsonBody(requestBody)).get

      status(newsResponse) must equalTo(FORBIDDEN)

    }

    "be created successfully with correct publisher_id" in new WithApplication {
      val wrongId = "123"
      val requestBody =
        Json.toJson(News(Some(999),
          93740362,
          "title",
          "created successfully with correct publisher_id", None,
          false, None,
          Some(777666),
          None,
          Some("3_93740362_1122")))
      val newsResponse = route(adminRequest(POST, "/kindergarten/93740362/admin/3_93740362_1122/news").withJsonBody(requestBody)).get

      status(newsResponse) must equalTo(OK)

      val checkResponse = route(adminRequest(GET, "/kindergarten/93740362/admin/3_93740362_1122/news?most=1")).get
      status(checkResponse) must equalTo(OK)
      Logger.info(checkResponse.toString)
      val response: JsValue = Json.parse(contentAsString(checkResponse))
      response match {
        case JsArray(arr) =>
          arr.length must equalTo(1)
          (arr(0) \ "content").as[String] must equalTo("created successfully with correct publisher_id")
        case _ => failure
      }

    }

    "be updated successfully with correct publisher_id" in new WithApplication {
      val wrongId = "123"
      val requestBody =
        Json.toJson(News(Some(7),
          93740362,
          "title",
          "updated successfully with correct publisher_id", None,
          false, None,
          Some(777666),
          None,
          Some("3_93740362_1122")))
      val newsResponse = route(adminRequest(POST, "/kindergarten/93740362/admin/3_93740362_1122/news/7").withJsonBody(requestBody)).get

      status(newsResponse) must equalTo(OK)

      val checkResponse = route(adminRequest(GET, "/kindergarten/93740362/admin/3_93740362_1122/news?most=1")).get
      status(checkResponse) must equalTo(OK)
      val response: JsValue = Json.parse(contentAsString(checkResponse))
      response match {
        case JsArray(arr) =>
          arr.length must equalTo(1)
          (arr(0) \ "content").as[String] must equalTo("updated successfully with correct publisher_id")
        case _ => failure
      }
    }
  }
}
