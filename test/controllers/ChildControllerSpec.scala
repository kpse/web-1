package controllers

import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import play.api.test.FakeRequest
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers._
import helper.TestSupport
import play.Logger
import models.ChildInfo
import play.api.db.DB
import anorm._
import play.api.Play.current
import org.specs2.specification.BeforeExample

@RunWith(classOf[JUnitRunner])
class ChildControllerSpec extends Specification with TestSupport {
  implicit val writes = Json.writes[ChildInfo]

  def loggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13402815317", "token" -> "1386425935574")
  }

  "Child" should {
    "check authentication first" in new WithApplication {

      val childResponse = route(FakeRequest(GET, "/kindergarten/93740362/child/1_1394545098158")).get

      status(childResponse) must equalTo(UNAUTHORIZED)

    }

    "be updated with icon_url" in new WithApplication {
      private val requestHeader = Json.toJson(new ChildInfo(Some("1_1394545098158"), "", "", "1999-01-02", 0, Some("url"), 777888, None, None, Some(93740362)))

      val updateResponse = route(loggedRequest(POST, "/kindergarten/93740362/child/1_1394545098158").withJsonBody(requestHeader)).get

      status(updateResponse) must equalTo(OK)
      contentType(updateResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(updateResponse))
      (response \ "nick").as[String] mustNotEqual empty
      (response \ "portrait").as[String] must equalTo("url")
      (response \ "birthday").as[String] must equalTo("1999-01-02")
    }

    "be updated with nick" in new WithApplication {
      private val requestHeader = Json.toJson(new ChildInfo(Some("1_1394545098158"), "", "new_nick_name", "1999-01-02", 0, Some("portrait"), 777888, None, None, Some(93740362)))

      val updateResponse = route(loggedRequest(POST, "/kindergarten/93740362/child/1_1394545098158").withJsonBody(requestHeader)).get

      status(updateResponse) must equalTo(OK)
      contentType(updateResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(updateResponse))
      (response \ "nick").as[String] must equalTo("new_nick_name")
      (response \ "portrait").as[String] mustNotEqual empty
      (response \ "birthday").as[String] must equalTo("1999-01-02")
    }

    "be updated with birthday" in new WithApplication {
      private val requestHeader = Json.toJson(new ChildInfo(Some("1_1394545098158"), "", "new_nick_name", "1999-01-02", 0, Some("portrait"), 777888, None, None, Some(93740362)))

      val updateResponse = route(loggedRequest(POST, "/kindergarten/93740362/child/1_1394545098158").withJsonBody(requestHeader)).get

      status(updateResponse) must equalTo(OK)
      contentType(updateResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(updateResponse))
      Logger.info(response.toString())
      (response \ "nick").as[String] mustNotEqual empty
      (response \ "portrait").as[String] mustNotEqual empty
      (response \ "birthday").as[String] must equalTo("1999-01-02")
    }

  }
}
