package controllers.V3

import helper.TestSupport
import models.Parent
import models.V3.{ParentExt, Relative}
import models.json_models.BindingNumber
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

      val id = (response \ "id").as[Long]

      val getRes = route(loggedRequest(GET, s"/api/v3/kindergarten/93740362/relative/$id")).get

      status(getRes) must equalTo(OK)
      contentType(getRes) must beSome.which(_ == "application/json")

      val response2: JsValue = Json.parse(contentAsString(getRes))
      (response2 \ "basic" \ "phone").as[String] must equalTo("22222222226")
    }

    implicit val w3 = Json.writes[BindingNumber]

    "fix up push information after updating" in new WithApplication {
      private val relativeWithPhone: JsValue = createRelative("22222222226", Some(129))
      val res = route(loggedRequest(POST, "/api/v3/kindergarten/93740362/relative/129").withBody(relativeWithPhone)).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val createBinding = Json.toJson(BindingNumber("22222222226", "", "", Some("3"), "0"))
      val bindingRes = route(FakeRequest(POST, "/api/v1/binding").withBody(createBinding)).get

      status(bindingRes) must equalTo(OK)

      val response: JsValue = Json.parse(contentAsString(bindingRes))
      (response \ "error_code").as[Long] must equalTo(0)
    }

    "reuse deleted phone number in creating" in new WithApplication {
      val firstPhone = route(loggedRequest(GET, "/api/v3/kindergarten/93740362/relative/1")).get
      val firstRelative: JsValue = Json.parse(contentAsString(firstPhone))
      private val phone: String = (firstRelative \ "basic" \ "phone").as[String]

      val response = route(loggedRequest(DELETE, "/api/v3/kindergarten/93740362/relative/1")).get

      status(response) must equalTo(OK)

      private val requestBody = Json.toJson(createRelative(phone, None, Some("some_new_parent_id")))

      val response2 = route(loggedRequest(POST, "/api/v3/kindergarten/93740362/relative").withBody(requestBody)).get

      status(response2) must equalTo(OK)
      Logger.info(s"reuse deleted phone number in creating contentAsString(response2) = ${contentAsString(response2)}")
      val newCreatedRelative: JsValue = Json.parse(contentAsString(response2))
      (newCreatedRelative \ "basic" \ "phone").as[String] must equalTo(phone)
    }

    "reuse deleted phone in updating" in new WithApplication {
      val firstPhone = route(loggedRequest(GET, "/api/v3/kindergarten/93740362/relative/1")).get
      val firstRelative: JsValue = Json.parse(contentAsString(firstPhone))
      private val phone: String = (firstRelative  \ "basic" \ "phone").as[String]

      val response = route(loggedRequest(DELETE, "/api/v3/kindergarten/93740362/relative/1")).get

      status(response) must equalTo(OK)

      private val requestBody = Json.toJson(createRelative(phone, Some(2)))

      val response2 = route(loggedRequest(POST, "/api/v3/kindergarten/93740362/relative/2").withBody(requestBody)).get

      status(response2) must equalTo(OK)

      Logger.info(contentAsString(response2))
      val UpdatedPhoneRelative: JsValue = Json.parse(contentAsString(response2))
      (UpdatedPhoneRelative \ "basic" \ "phone").as[String] must equalTo(phone)

    }

    "reuse deleted phone from different school in creation" in new WithApplication {
      private val requestBody = Json.toJson(createRelative("22222222225"))

      val response2 = route(loggedRequest(POST, "/api/v3/kindergarten/93740362/relative").withBody(requestBody)).get

      status(response2) must equalTo(OK)

      Logger.info(contentAsString(response2))
      val createdPhoneRelative: JsValue = Json.parse(contentAsString(response2))
      (createdPhoneRelative \ "basic" \ "phone").as[String] must equalTo("22222222225")

    }

    "remove video member while deleting" in new WithApplication {
      val firstPhone = route(loggedRequest(GET, "/api/v3/kindergarten/93740362/relative/1")).get
      val firstRelative: JsValue = Json.parse(contentAsString(firstPhone))

      private val pid: String = (firstRelative  \ "basic" \ "parent_id").as[String]

      val VideoMemberexists = route(loggedRequest(GET, s"/api/v1/kindergarten/93740362/video_member/${pid}")).get

      status(VideoMemberexists) must equalTo(OK)

      val response = route(loggedRequest(DELETE, "/api/v3/kindergarten/93740362/relative/1")).get

      status(response) must equalTo(OK)

      val VideoMemberDoesNotexist = route(loggedRequest(GET, s"/api/v1/kindergarten/93740362/video_member/${pid}")).get

      status(VideoMemberDoesNotexist) must equalTo(NOT_FOUND)

    }

  }

  def createRelative(phone: String, id: Option[Long] = None, parentId: Some[String] = Some("2_93740362_1023")): JsValue = {
    Json.toJson(Relative(id, createParent(phone, id, parentId), Some(createExt)))
  }

  def createParent(phone: String, id: Option[Long] = None, parentId: Some[String]): Parent = {
    Parent(parentId, 93740362, "name", phone, None, 1, "1999-01-02", Some(0), Some(0), Some(1), Some("com"), None, None, id)
  }

  def createExt = {
    ParentExt(Some("name"), Some("123"), Some("China"), Some("029xxxxxxxx"), Some("memo"))
  }
}
