package controllers.V3

import helper.TestSupport
import models.Parent
import models.V3.{ParentExt, Relative}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
object RelativeControllerSpec extends Specification with TestSupport {
  def loggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654680", "token" -> "1386849160798")
  }

  "Relative" should {
    "check authentication first" in new WithApplication {
      val response = route(FakeRequest(GET, "/api/v3/kindergarten/123/relative/123")).get

      status(response) must equalTo(UNAUTHORIZED)

    }

    "check basic information existing before creating" in new WithApplication {
      val res = route(loggedRequest(POST, "/api/v3/kindergarten/93740362/relative").withBody(createRelative("13402815317"))).get

      status(res) must equalTo(BAD_REQUEST)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "error_code").as[Long] must equalTo(6)
    }

    "ignore deleted basic information in before creating checking" in new WithApplication {
      private val relativeWithPhone: JsValue = createRelative("22222222226")
      val res = route(loggedRequest(POST, "/api/v3/kindergarten/93740362/relative").withBody(relativeWithPhone)).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "basic" \ "phone").as[String] must equalTo("22222222226")

      val id =  (response \ "id").as[Long]

      val getRes = route(loggedRequest(GET, s"/api/v3/kindergarten/93740362/relative/$id")).get

      status(getRes) must equalTo(OK)
      contentType(getRes) must beSome.which(_ == "application/json")

      val response2: JsValue = Json.parse(contentAsString(getRes))
      (response2 \ "basic" \ "phone").as[String] must equalTo("22222222226")
    }

  }

  def createRelative(phone: String): JsValue = {
    Json.toJson(Relative(None, createParent(phone), Some(createExt)))
  }

  def createParent(phone: String): Parent = {
    Parent(None, 93740362, "name", phone, None, 1, "1999-01-02", Some(0), Some(0), Some(1), Some("com"))
  }

  def createExt = {
    ParentExt(Some("name"), Some("123"), Some("China"), Some("029xxxxxxxx"), Some("memo"))
  }
}
