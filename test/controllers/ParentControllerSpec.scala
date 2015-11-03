package controllers

import _root_.helper.TestSupport
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import play.Logger
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.libs.json.{JsArray, JsValue, Json}
import models.{ParentPhoneCheck, ChildInfo, Parent}

@RunWith(classOf[JUnitRunner])
class ParentControllerSpec extends Specification with TestSupport {
  implicit val writes = Json.writes[Parent]
  implicit val read = Json.reads[ParentPhoneCheck]
  implicit val writes2 = Json.writes[ParentPhoneCheck]

  def principalLoggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_1122", "username" -> "e0001")
  }

  def twoClassesManagerLoggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_3344", "username" -> "e0002")
  }

  def noAccessLoggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_9977", "username" -> "e0003")
  }

  "Parent" should {
    "check authentication first" in new WithApplication {

      val response = route(FakeRequest(GET, "/kindergarten/93740362/parent")).get

      status(response) must equalTo(UNAUTHORIZED)
    }

    "show all parents to principal" in new WithApplication {
      val response = route(principalLoggedRequest(GET, "/kindergarten/93740362/parent")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must greaterThanOrEqualTo(7)
          (arr(0) \ "parent_id").as[String].isEmpty must beFalse
        case _ => failure
      }
    }

    "show partial parents to manager" in new WithApplication {
      val response = route(twoClassesManagerLoggedRequest(GET, "/kindergarten/93740362/parent")).get
      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must equalTo(8)
          (arr.head \ "parent_id").as[String] must equalTo("2_93740362_000")
        case _ => failure
      }
    }

    "show all unconnected parents to no access teacher" in new WithApplication {

      val response = route(noAccessLoggedRequest(GET, "/kindergarten/93740362/parent")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must beEqualTo(3)
        case _ => failure
      }
    }

    "show no parent to no access teacher even specific class id" in new WithApplication {

      val response = route(noAccessLoggedRequest(GET, "/kindergarten/93740362/parent?class_id=777666")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must beEqualTo(0)
        case _ => failure
      }
    }

    "show no parent to manager when specific non-managed class" in new WithApplication {

      val response = route(twoClassesManagerLoggedRequest(GET, "/kindergarten/93740362/parent?class_id=777999")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must beEqualTo(0)
        case _ => failure
      }
    }

    "show all parents to manager in specific class" in new WithApplication {

      val response = route(twoClassesManagerLoggedRequest(GET, "/kindergarten/93740362/parent?class_id=777666")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must beEqualTo(3)
        case _ => failure
      }
    }

    "show all parents to principal in specific class" in new WithApplication {

      val response = route(principalLoggedRequest(GET, "/kindergarten/93740362/parent?class_id=777666")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must equalTo(3)
        case _ => failure
      }
    }

    "show all unconnected parents to no privilege teachers" in new WithApplication {

      val response = route(noAccessLoggedRequest(GET, "/kindergarten/93740362/parent?connected=false")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must equalTo(3)
        case _ => failure
      }
    }

    "show no parent to no privilege teachers if they wanna check connected parants" in new WithApplication {

      val response = route(noAccessLoggedRequest(GET, "/kindergarten/93740362/parent?connected=true")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.isEmpty must beTrue
        case _ => failure
      }
    }

    "show all unconnected parents to all kinds of privilege: manager" in new WithApplication {

      val response = route(twoClassesManagerLoggedRequest(GET, "/kindergarten/93740362/parent?connected=false")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must equalTo(3)
        case _ => failure
      }
    }

    "show all unconnected parents to all kinds of privilege: principal" in new WithApplication {

      val response = route(principalLoggedRequest(GET, "/kindergarten/93740362/parent?connected=false")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must equalTo(3)
        case _ => failure
      }
    }

    "phone check successfully when phone number does not exist" in new WithApplication {
      private val requestBody = Json.toJson(ParentPhoneCheck(None, "1234567"))

      val checkResponse = route(principalLoggedRequest(POST, "/api/v1/phone_check/1234567").withJsonBody(requestBody)).get

      status(checkResponse) must equalTo(OK)
      contentType(checkResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(checkResponse))
      (response \ "error_code").as[Int] must equalTo(0)

    }

    "phone check successfully when phone number and id are matching" in new WithApplication {
      private val requestBody = Json.toJson(ParentPhoneCheck(Some("2_93740362_123"), "13402815317"))

      val checkResponse = route(principalLoggedRequest(POST, "/api/v1/phone_check/13402815317").withJsonBody(requestBody)).get

      status(checkResponse) must equalTo(OK)
      contentType(checkResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(checkResponse))
      (response \ "error_code").as[Int] must equalTo(0)

    }

    "phone check fails when phone number and id are not matching" in new WithApplication {
      private val requestBody = Json.toJson(ParentPhoneCheck(Some("2_93740362_456"), "13402815317"))

      val checkResponse = route(principalLoggedRequest(POST, "/api/v1/phone_check/13402815317").withJsonBody(requestBody)).get

      status(checkResponse) must equalTo(OK)
      contentType(checkResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(checkResponse))
      (response \ "error_code").as[Int] must equalTo(1)

    }

    "phone check fails when phone number exists and id is missing" in new WithApplication {
      private val requestBody = Json.toJson(ParentPhoneCheck(None, "13402815317"))

      val checkResponse = route(principalLoggedRequest(POST, "/api/v1/phone_check/13402815317").withJsonBody(requestBody)).get

      status(checkResponse) must equalTo(OK)
      contentType(checkResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(checkResponse))
      (response \ "error_code").as[Int] must equalTo(1)

    }

    "be updated successfully with new number in create route" in new WithApplication {
      private val requestHeader = createParent("13408654682")
      val response = route(principalLoggedRequest(POST, "/kindergarten/93740362/parent").withJsonBody(requestHeader)).get

      status(response) must equalTo(OK)
    }

    "be updated successfully with new number in update route" in new WithApplication {
      private val requestHeader = createParent("14408654682")
      val response = route(principalLoggedRequest(POST, "/kindergarten/93740362/parent/14408654682").withJsonBody(requestHeader)).get

      status(response) must equalTo(OK)
    }

    "be created successfully with new number and id in create route" in new WithApplication {
      private val requestHeader = createParent("14408654683", Some("2_93740362_899"))
      val response = route(principalLoggedRequest(POST, "/kindergarten/93740362/parent").withJsonBody(requestHeader)).get

      status(response) must equalTo(OK)
    }

    "be created successfully with new number and id in update route" in new WithApplication {
      private val requestHeader = createParent("15408654683", Some("2_93740362_899"))
      val response = route(principalLoggedRequest(POST, "/kindergarten/93740362/parent/15408654683").withJsonBody(requestHeader)).get

      status(response) must equalTo(OK)
    }

    "report error when phone number does not match the url" in new WithApplication {
      private val requestHeader = createParent("111111")
      val response = route(principalLoggedRequest(POST, "/kindergarten/93740362/parent/222222").withJsonBody(requestHeader)).get

      status(response) must equalTo(BAD_REQUEST)
      (Json.parse(contentAsString(response)) \ "error_msg").as[String] must equalTo("与url中电话号码不匹配。")
    }

    "report error when given parent id does not exist" in new WithApplication {
      private val requestHeader = createParent("13408654680", Some("2_93740362_799"))
      val response = route(principalLoggedRequest(POST, "/kindergarten/93740362/parent/13408654680").withJsonBody(requestHeader)).get

      status(response) must equalTo(BAD_REQUEST)
      (Json.parse(contentAsString(response)) \ "error_msg").as[String] must equalTo(s"手机号码‘13408654680’已经存在，请检查输入号码是否正确")
    }

    "be updated successfully when no parent id is given in create route" in new WithApplication {
      private val requestHeader = createParent("13408654680", None)
      val response = route(principalLoggedRequest(POST, "/kindergarten/93740362/parent").withJsonBody(requestHeader)).get

      status(response) must equalTo(OK)
    }

    "be updated successfully when no parent id is given in update route" in new WithApplication {
      private val requestHeader = createParent("13408654680", None)
      val response = route(principalLoggedRequest(POST, "/kindergarten/93740362/parent/13408654680").withJsonBody(requestHeader)).get

      status(response) must equalTo(OK)
    }

    "report error when wrong number is given to update route" in new WithApplication {
      private val requestHeader = createParent("13408654690", None)
      val response = route(principalLoggedRequest(POST, "/kindergarten/93740362/parent/13408654680").withJsonBody(requestHeader)).get

      status(response) must equalTo(BAD_REQUEST)
      (Json.parse(contentAsString(response)) \ "error_msg").as[String] must equalTo("与url中电话号码不匹配。")
    }

    "report error when number exists in other school in update route" in new WithApplication {
      private val requestHeader = createParent("22222222225", None)
      val response = route(principalLoggedRequest(POST, "/kindergarten/93740362/parent/22222222225").withJsonBody(requestHeader)).get

      status(response) must equalTo(BAD_REQUEST)
      (Json.parse(contentAsString(response)) \ "error_msg").as[String] must equalTo("手机号码‘22222222225’已经在别的学校注册，目前幼乐宝不支持同一家长在多家幼儿园注册，请联系幼乐宝技术支持4009984998")
    }

    "report error when number exists in other school in create route" in new WithApplication {
      private val requestHeader = createParent("22222222225", None)
      val response = route(principalLoggedRequest(POST, "/kindergarten/93740362/parent").withJsonBody(requestHeader)).get

      status(response) must equalTo(BAD_REQUEST)
      (Json.parse(contentAsString(response)) \ "error_msg").as[String] must equalTo("手机号码‘22222222225’已经在别的学校注册，目前幼乐宝不支持同一家长在多家幼儿园注册，请联系幼乐宝技术支持4009984998")
    }

    "report ok when parent has been already deleted in update route" in new WithApplication {
      private val requestHeader = createDeletedParent("16408654680", Some("new_id"))
      val response = route(principalLoggedRequest(POST, "/kindergarten/93740362/parent/16408654680").withJsonBody(requestHeader)).get

      status(response) must equalTo(OK)
      (Json.parse(contentAsString(response)) \ "error_msg").as[String] must equalTo("忽略已删除数据。")
    }

    "report ok when parent has been already deleted in create route" in new WithApplication {
      private val requestHeader = createDeletedParent("16408654680", Some("new_id"))
      val response = route(principalLoggedRequest(POST, "/kindergarten/93740362/parent").withJsonBody(requestHeader)).get

      status(response) must equalTo(OK)
      (Json.parse(contentAsString(response)) \ "error_msg").as[String] must equalTo("忽略已删除数据。")
    }
  }

  def createParent(phone: String, id: Option[String] = Some("2_93740362_789")): JsValue = {
    Json.toJson(Parent(id, 93740362, "name", phone, None, 1, "1999-01-02", Some(0), Some(0), Some(1), Some("com")))
  }

  def createMember(phone: String, id: Option[String] = Some("2_93740362_789")): JsValue = {
    Json.toJson(Parent(id, 93740362, "name", phone, None, 1, "1999-01-02", Some(0), Some(1), Some(1), Some("com")))
  }

  def createDeletedParent(phone: String, id: Option[String] = Some("2_93740362_789")): JsValue = {
    Json.toJson(Parent(id, 93740362, "name", phone, None, 1, "1999-01-02", Some(0), Some(0), Some(0), Some("com")))
  }
}
