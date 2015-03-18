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
class NewsControllerV2Spec extends Specification with TestSupport {
  def loggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654680", "token" -> "1386849160798")
  }

  def adminRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "login_name", "id" -> "3_93740362_1122")
  }

  "News" should {
    "check authentication first" in new WithApplication {

      val newsResponse = route(FakeRequest(GET, "/api/v2/kindergarten/93740362/news")).get

      status(newsResponse) must equalTo(UNAUTHORIZED)

    }

    "report less than 25 pieces by default" in new WithApplication {

      val newsResponse = route(loggedRequest(GET, "/api/v2/kindergarten/93740362/news")).get

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

      val newsResponse = route(loggedRequest(GET, "/api/v2/kindergarten/93740362/news?from=5")).get

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

      val newsResponse = route(loggedRequest(GET, "/api/v2/kindergarten/93740362/news?to=5")).get

      status(newsResponse) must equalTo(OK)
      contentType(newsResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(newsResponse))
      response match {
        case JsArray(arr) =>
          arr.length must greaterThan(1)
          (arr(0) \ "news_id").as[Long] must equalTo(4L)
        case _ => failure
      }

    }

    "accept parameter most" in new WithApplication {

      val newsResponse = route(loggedRequest(GET, "/api/v2/kindergarten/93740362/news?most=2")).get

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

    "be created successfully with tags" in new WithApplication {
      val requestBody =
        Json.toJson(News(None,
          93740362,
          "title",
          "created successfully with tags", None,
          false, None,
          Some(777666),
          None,
          Some("3_93740362_1122"), Some(false), List("作业", "活动")))
      val newsResponse = route(adminRequest(POST, "/api/v2/kindergarten/93740362/news").withJsonBody(requestBody)).get

      status(newsResponse) must equalTo(OK)
      val lastCreated: JsValue = Json.parse(contentAsString(newsResponse))
      val newId = (lastCreated \ "news_id").as[Long]

      val checkResponse = route(adminRequest(GET, s"/api/v2/kindergarten/93740362/news/$newId?tag=1")).get
      status(checkResponse) must equalTo(OK)
      Logger.info(checkResponse.toString)
      val response: JsValue = Json.parse(contentAsString(checkResponse))
      response match {
        case news: JsValue =>
          (news \ "content").as[String] must equalTo("created successfully with tags")
          (news \ "tags").as[List[String]] must containTheSameElementsAs(List("作业", "活动"))
        case _ => failure
      }

    }

    "be updated successfully with tags" in new WithApplication {
      val requestBody =
        Json.toJson(News(Some(7),
          93740362,
          "title",
          "updated successfully with tags", None,
          false, None,
          Some(777666),
          None,
          Some("3_93740362_1122"), Some(false), List("作业", "活动")))
      val newsResponse = route(adminRequest(POST, "/api/v2/kindergarten/93740362/news/7").withJsonBody(requestBody)).get

      status(newsResponse) must equalTo(OK)

      val checkResponse = route(adminRequest(GET, "/api/v2/kindergarten/93740362/news/7?tag=1")).get
      status(checkResponse) must equalTo(OK)
      val response: JsValue = Json.parse(contentAsString(checkResponse))
      response match {
        case news: JsValue =>
          (news \ "content").as[String] must equalTo("updated successfully with tags")
          (news \ "tags").as[List[String]] must containTheSameElementsAs(List("作业", "活动"))
        case _ => failure
      }
    }

    "accept parameter tag" in new WithApplication {

      val newsResponse = route(loggedRequest(GET, "/api/v2/kindergarten/93740362/news/1?tag=1")).get

      status(newsResponse) must equalTo(OK)
      contentType(newsResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(newsResponse))
      response match {
        case news: JsValue =>
          (news \ "news_id").as[Long] must equalTo(1L)
          (news \ "tags").as[List[String]] must containTheSameElementsAs(List("作业", "活动"))
        case _ => failure
      }

    }
  }
}
