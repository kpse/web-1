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

  def principalRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "principal", "id" -> "3_93740362_1122")
  }

  def managerTeacherRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "manager", "id" -> "3_93740362_3344")
  }

  def nonManagerTeacherRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "nonManager", "id" -> "3_93740362_9977")
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

      val response: Seq[JsValue] = Json.parse(contentAsString(newsResponse)).as[JsArray].value
      response.length must be lessThan 25
      (response.head \ "news_id").as[Long] must equalTo(6L)
    }

    "preview like index" in new WithApplication {

      val newsResponse = route(loggedRequest(GET, "/api/v2/kindergarten/93740362/news_preview")).get

      status(newsResponse) must equalTo(OK)
      contentType(newsResponse) must beSome.which(_ == "application/json")

      val response: Seq[JsValue] = Json.parse(contentAsString(newsResponse)).as[JsArray].value
      response.length must be lessThan 25
      (response.head \ "id").as[Long] must equalTo(6L)
    }

    "accept parameter from" in new WithApplication {

      val newsResponse = route(loggedRequest(GET, "/api/v2/kindergarten/93740362/news?from=5")).get

      status(newsResponse) must equalTo(OK)
      contentType(newsResponse) must beSome.which(_ == "application/json")

      val response: Seq[JsValue] = Json.parse(contentAsString(newsResponse)).as[JsArray].value
      response.length must equalTo(1)
      (response.head \ "news_id").as[Long] must equalTo(6L)

    }

    "accept parameter from in preview" in new WithApplication {

      val newsResponse = route(loggedRequest(GET, "/api/v2/kindergarten/93740362/news_preview?from=5")).get

      status(newsResponse) must equalTo(OK)
      contentType(newsResponse) must beSome.which(_ == "application/json")

      val response: Seq[JsValue] = Json.parse(contentAsString(newsResponse)).as[JsArray].value
      response.length must equalTo(1)
      (response.head \ "id").as[Long] must equalTo(6L)

    }

    "accept parameter to" in new WithApplication {

      val newsResponse = route(loggedRequest(GET, "/api/v2/kindergarten/93740362/news?to=5")).get

      status(newsResponse) must equalTo(OK)
      contentType(newsResponse) must beSome.which(_ == "application/json")

      val response: Seq[JsValue] = Json.parse(contentAsString(newsResponse)).as[JsArray].value
      response.length must greaterThan(1)
      (response.head \ "news_id").as[Long] must equalTo(4L)

    }

    "accept parameter most" in new WithApplication {

      val newsResponse = route(loggedRequest(GET, "/api/v2/kindergarten/93740362/news?most=2")).get

      status(newsResponse) must equalTo(OK)
      contentType(newsResponse) must beSome.which(_ == "application/json")

      val response: Seq[JsValue] = Json.parse(contentAsString(newsResponse)).as[JsArray].value
      response.length must equalTo(2)
      (response.head \ "news_id").as[Long] must equalTo(6L)

    }
    implicit val writes = Json.writes[News]

    "be created successfully with tags" in new WithApplication {
      val requestBody =
        Json.toJson(News(None,
          93740362,
          "title",
          "created successfully with tags", None,
          published = false, None,
          Some(777666),
          None,
          Some("3_93740362_1122"), Some(false), List("作业", "活动")))
      val newsResponse = route(principalRequest(POST, "/api/v2/kindergarten/93740362/admin/3_93740362_1122/news").withJsonBody(requestBody)).get

      status(newsResponse) must equalTo(OK)
      val lastCreated: JsValue = Json.parse(contentAsString(newsResponse))
      val newId = (lastCreated \ "news_id").as[Long]

      val checkResponse = route(principalRequest(GET, s"/api/v2/kindergarten/93740362/news/$newId?tag=1")).get
      status(checkResponse) must equalTo(OK)
      Logger.info(checkResponse.toString)
      val response: JsValue = Json.parse(contentAsString(checkResponse))
      (response \ "content").as[String] must equalTo("created successfully with tags")
      (response \ "tags").as[List[String]] must containTheSameElementsAs(List("作业", "活动"))

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
      val newsResponse = route(principalRequest(POST, "/api/v2/kindergarten/93740362/admin/3_93740362_1122/news/7").withJsonBody(requestBody)).get

      status(newsResponse) must equalTo(OK)

      val checkResponse = route(principalRequest(GET, "/api/v2/kindergarten/93740362/news/7?tag=1")).get
      status(checkResponse) must equalTo(OK)
      val response: JsValue = Json.parse(contentAsString(checkResponse))
      (response \ "content").as[String] must equalTo("updated successfully with tags")
      (response \ "tags").as[List[String]] must containTheSameElementsAs(List("作业", "活动"))
    }

    "accept parameter tag" in new WithApplication {

      val newsResponse = route(loggedRequest(GET, "/api/v2/kindergarten/93740362/news/1?tag=1")).get

      status(newsResponse) must equalTo(OK)
      contentType(newsResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(newsResponse))
      (response \ "news_id").as[Long] must equalTo(1L)
      (response \ "tags").as[List[String]] must containTheSameElementsAs(List("作业", "活动"))
    }

    "not be deleted by ineligible teacher if news is to public to the whole school" in new WithApplication {

      val deletedResponse = route(nonManagerTeacherRequest(DELETE, "/api/v2/kindergarten/93740362/admin/3_93740362_9977/news/1")).get
      Logger.info(s"contentAsString(deletedResponse) = ${contentAsString(deletedResponse)}")
      status(deletedResponse) must equalTo(FORBIDDEN)
      contentType(deletedResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(deletedResponse))
      (response \ "error_code").as[Int] must equalTo(42)

    }

    "not be deleted by ineligible teacher even news is published to class" in new WithApplication {

      val deletedResponse = route(nonManagerTeacherRequest(DELETE, "/api/v2/kindergarten/93740362/admin/3_93740362_9977/news/8")).get

      status(deletedResponse) must equalTo(FORBIDDEN)
      contentType(deletedResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(deletedResponse))
      (response \ "error_code").as[Int] must equalTo(42)

    }

    "be deleted by principal" in new WithApplication {

      val deletedResponse = route(principalRequest(DELETE, "/api/v2/kindergarten/93740362/admin/3_93740362_1122/news/1")).get

      status(deletedResponse) must equalTo(OK)
      contentType(deletedResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(deletedResponse))
      (response \ "error_code").as[Int] must equalTo(0)

    }

    "not be deleted by teacher if the news scope is the whole school" in new WithApplication {

      val deletedResponse = route(managerTeacherRequest(DELETE, "/api/v2/kindergarten/93740362/admin/3_93740362_3344/news/1")).get

      status(deletedResponse) must equalTo(FORBIDDEN)
      contentType(deletedResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(deletedResponse))
      (response \ "error_code").as[Int] must equalTo(42)

    }

    "be deleted by eligible teacher" in new WithApplication {

      val deletedResponse = route(managerTeacherRequest(DELETE, "/api/v2/kindergarten/93740362/admin/3_93740362_3344/news/8")).get

      status(deletedResponse) must equalTo(OK)
      contentType(deletedResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(deletedResponse))
      (response \ "error_code").as[Int] must equalTo(0)

    }

  }
}
