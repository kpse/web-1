package controllers

import _root_.helper.TestSupport
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.libs.json._
import models.json_models.MobileLogin
import models.json_models.CheckPhone
import models.json_models.BindingNumber

@RunWith(classOf[JUnitRunner])
class AuthenticationSpec extends Specification with TestSupport {
  implicit val loginWrites = Json.writes[MobileLogin]

  "Authentication" should {
    "log mobile in" in new WithApplication {

      private val json = Json.toJson(MobileLogin("13333333333", "password"))
      val loginResponse = route(FakeRequest(POST, "/login.do").withJsonBody(json)).get

      status(loginResponse) must equalTo(OK)
      contentType(loginResponse) must beSome("application/json")

      val response: JsValue = Json.parse(contentAsString(loginResponse))
      (response \ "error_code").as[Int] must equalTo(0)
      (response \ "account_name").as[String] must equalTo("13333333333")
      (response \ "access_token").as[String] must equalTo("0")
      (response \ "username").as[String] must be matching "测试"
    }
    implicit val checkWrites = Json.writes[CheckPhone]

    "phone number is ok even for nonactive" in new WithApplication {
      private val json = Json.toJson(CheckPhone("13408654683"))

      val validateResponse = route(FakeRequest(POST, "/checkphonenum.do").withJsonBody(json)).get

      status(validateResponse) must equalTo(OK)
      contentType(validateResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(validateResponse))
      (response \ "check_phone_result").as[String] must equalTo("1102")
    }

    "validate phone number for active number" in new WithApplication {
      private val json = Json.toJson(CheckPhone("13408654680"))

      val validateResponse = route(FakeRequest(POST, "/checkphonenum.do").withJsonBody(json)).get

      status(validateResponse) must equalTo(OK)
      contentType(validateResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(validateResponse))
      (response \ "check_phone_result").as[String] must equalTo("1102")
    }

    "validate phone number for wrong number" in new WithApplication {
      private val json = Json.toJson(CheckPhone("9191919"))

      val validateResponse = route(FakeRequest(POST, "/checkphonenum.do").withJsonBody(json)).get

      status(validateResponse) must equalTo(OK)
      contentType(validateResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(validateResponse))
      (response \ "check_phone_result").as[String] must equalTo("1100")
    }


    implicit val bindingWrites = Json.writes[BindingNumber]

    "bind phone number" in new WithApplication {
      val phone = "13333333333"
      val user_id = "12334"
      val channel_id = "000000"
      private val json = Json.toJson(BindingNumber(phone, user_id, channel_id, None, "0"))

      val bindingResponse = route(FakeRequest(POST, "/receiveBindInfo.do").withJsonBody(json)).get

      status(bindingResponse) must equalTo(OK)
      contentType(bindingResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(bindingResponse))
      (response \ "error_code").as[Int] must equalTo(0)
      (response \ "account_name").as[String] must equalTo(phone)
      (response \ "access_token").as[String] mustNotEqual empty
      (response \ "username").as[String] must be matching "测试"
      (response \ "school_id").as[Long] must equalTo(93740362)
    }

    "reject mobile when wrong password" in new WithApplication {

      private val json = Json.toJson(MobileLogin("13333333333", "wrong password"))

      val loginResponse = route(FakeRequest(POST, "/login.do").withJsonBody(json)).get

      status(loginResponse) must equalTo(OK)
      contentType(loginResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(loginResponse))
      (response \ "error_code").as[Int] must equalTo(1)
      (response \ "account_name").as[String] must equalTo("")
      (response \ "access_token").as[String] must equalTo("")
      (response \ "username").as[String] must equalTo("")
    }

    "validate for expired phone number" in new WithApplication {

      private val json = Json.toJson(CheckPhone("22222222222"))

      val validateResponse = route(FakeRequest(POST, "/checkphonenum.do").withJsonBody(json)).get

      status(validateResponse) must equalTo(OK)
      contentType(validateResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(validateResponse))
      (response \ "check_phone_result").as[String] must equalTo("1101")
    }

    "append the IM token in binding response" in new WithApplication {
      val phone = "13333333333"
      val user_id = "12334"
      val channel_id = "000000"
      private val json = Json.toJson(BindingNumber(phone, user_id, channel_id, None, "0"))

      val bindingResponse = route(FakeRequest(POST, "/api/v1/binding").withJsonBody(json)).get

      status(bindingResponse) must equalTo(OK)
      contentType(bindingResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(bindingResponse))
      (response \ "error_code").as[Int] must equalTo(0)
      (response \ "im_token" \ "source").as[String] must equalTo("db")
    }

    "append the IM token in employee login response" in new WithApplication {
      private val json = Json.toJson(MobileLogin("e0001", "secret"))

      val bindingResponse = route(FakeRequest(POST, "/employee_login.do").withJsonBody(json)).get

      status(bindingResponse) must equalTo(OK)
      contentType(bindingResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(bindingResponse))
      (response \ "login_name").as[String] must equalTo("e0001")
      (response \ "privilege_group").as[String] must equalTo("principal")
      (response \ "im_token" \ "source").as[String] must equalTo("db")
    }

    "reject wrong employee password" in new WithApplication {
      private val json = Json.toJson(MobileLogin("e0001", "wrong"))

      val bindingResponse = route(FakeRequest(POST, "/employee_login.do").withJsonBody(json)).get

      status(bindingResponse) must equalTo(FORBIDDEN)
    }

  }
}
