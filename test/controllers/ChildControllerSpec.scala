package controllers

import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import play.api.test.FakeRequest
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers._
import models.json_models.ChildUpdate
import helper.TestSupport
import play.Logger

@RunWith(classOf[JUnitRunner])
class ChildControllerSpec extends Specification with TestSupport {
  implicit val writes = Json.writes[ChildUpdate]
  def requestWithSession(method: String, url: String) = FakeRequest(method, url)
    .withSession("username" -> "13402815317", "token" -> "1386425935574")
  "Child" should {

    "be updated with icon_url" in new WithApplication {

      private val requestHeader = Json.toJson(new ChildUpdate(None, None, Some("icon_url")))

      val updateResponse = route(requestWithSession(POST, "/kindergarten/93740362/parent/13408654680/child/1_1394545098158").withJsonBody(requestHeader)).get

      status(updateResponse) must equalTo(OK)
      contentType(updateResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(updateResponse))
      (response \ "error_code").as[Int] must equalTo(0)
      (response \ "child_info" \ "nick").as[String] mustNotEqual empty
      (response \ "child_info" \ "icon_url").as[String] must equalTo("icon_url")
      (response \ "child_info" \ "birthday").as[Long] must greaterThan(0L)
    }

    "be updated with nick" in new WithApplication {

      private val requestHeader = Json.toJson(new ChildUpdate(Some("new_nick_name"), None, None))

      val updateResponse = route(requestWithSession(POST, "/kindergarten/93740362/parent/13408654680/child/1_1394545098158").withJsonBody(requestHeader)).get

      status(updateResponse) must equalTo(OK)
      contentType(updateResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(updateResponse))
      (response \ "error_code").as[Int] must equalTo(0)
      (response \ "child_info" \ "nick").as[String] must equalTo("new_nick_name")
      (response \ "child_info" \ "icon_url").as[String] mustNotEqual empty
      (response \ "child_info" \ "birthday").as[Long] must greaterThan(0L)
    }

    "be updated with birthday" in new WithApplication {

      private val requestHeader = Json.toJson(new ChildUpdate(None, Some(1167580800000L), None))

      val updateResponse = route(requestWithSession(POST, "/kindergarten/93740362/parent/13408654680/child/1_1394545098158").withJsonBody(requestHeader)).get

      status(updateResponse) must equalTo(OK)
      contentType(updateResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(updateResponse))
      Logger.info(response.toString())
      (response \ "error_code").as[Int] must equalTo(0)
      (response \ "child_info" \ "nick").as[String] mustNotEqual empty
      (response \ "child_info" \ "icon_url").as[String] mustNotEqual empty
      (response \ "child_info" \ "birthday").as[Long] must greaterThan(0L)
    }

  }
}
