package controllers

import _root_.helper.TestSupport
import models.json_models.SchoolIntro._
import models.json_models.{CreatingSchool, PrincipalOfSchool, SchoolIntro}
import models.{ChargeInfo, ConfigItem, SchoolConfig}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class SchoolSummaryControllerSpec extends Specification with TestSupport {
  def requestWithSession(method: String, url: String) = FakeRequest(method, url).withSession("id" -> "3_93740362_9971", "username" -> "admin")
  def requestByOperator(method: String, url: String) = FakeRequest(method, url).withSession("id" -> "3_93740362_9972", "username" -> "operator")

  "SchoolSummary" should {
    "check authentication first" in new WithApplication {

      val previewResponse = route(FakeRequest(GET, "/kindergarten/93740362/preview")).get

      status(previewResponse) must equalTo(UNAUTHORIZED)

    }
    "report preview" in new WithApplication {

      val previewResponse = route(requestWithSession(GET, "/kindergarten/93740362/preview")).get

      status(previewResponse) must equalTo(OK)
      contentType(previewResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(previewResponse))
      val first = response(0)
      (first \ "error_code").as[Int] must equalTo(0)
      (first \ "school_id").as[Long] must equalTo(93740362)
      (first \ "timestamp").as[Long] must greaterThan(0L)
    }

    "report detail" in new WithApplication {

      val detailResponse = route(requestWithSession(GET, "/kindergarten/93740362/detail")).get

      status(detailResponse) must equalTo(OK)
      contentType(detailResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(detailResponse))
      (response \ "error_code").as[Int] must equalTo(0)
      (response \ "school_id").as[Long] must equalTo(93740362)
      (response \ "school_info" \ "timestamp").as[Long] must greaterThan(0L)
      (response \ "school_info" \ "phone").as[String] must startingWith("13")
      (response \ "school_info" \ "school_logo_url").as[String] must startingWith("http")

    }
    "SchoolSummary" should {
      "be deleted by operator" in new WithApplication {

        val deleteResponse = route(requestByOperator(DELETE, "/kindergarten/93740362")).get

        status(deleteResponse) must equalTo(OK)

        val detailResponse = route(requestByOperator(GET, "/kindergarten/93740362/detail")).get

        status(detailResponse) must equalTo(NOT_FOUND)

        private val charge: ChargeInfo = ChargeInfo(93740362, 10, "2016-01-01", 1, 1)
        private val json: JsValue = Json.toJson(CreatingSchool(93740362, "100", "full name", "", PrincipalOfSchool("admin_this", "admin_this", "139999999999"), charge, "", Some("93740362")))
        val createResponse = route(requestByOperator(POST, "/kindergarten").withBody(json)).get

        status(createResponse) must equalTo(OK)
        val response: JsValue = Json.parse(contentAsString(createResponse))
        (response \ "school_id").as[Long] must equalTo(93740362)

        val detailResponse2 = route(requestByOperator(GET, "/kindergarten/93740362/detail")).get

        status(detailResponse2) must equalTo(OK)
        val r2: JsValue = Json.parse(contentAsString(detailResponse2))
        (r2 \ "school_id").as[Long] must equalTo(93740362)

      }

      "be updated by operator" in new WithApplication {

        private val json: JsValue = Json.toJson(SchoolIntro(93740362, "100", 0, "desc", "", "new name", None, Some("new address"), Some("full name"), Some(List(ConfigItem("a", "b")))))
        val updated = route(requestByOperator(POST, "/kindergarten/93740362").withBody(json)).get

        status(updated) must equalTo(OK)

        val detailResponse2 = route(requestByOperator(GET, "/kindergarten/93740362/detail")).get

        status(detailResponse2) must equalTo(OK)
        val r2: JsValue = Json.parse(contentAsString(detailResponse2))
        (r2 \ "school_id").as[Long] must equalTo(93740362)
        (r2 \ "school_info" \ "properties").as[List[ConfigItem]] must contain(ConfigItem("a", "b", Some("global")))
      }

      "not be deleted by teacher" in new WithApplication {

        val deleteResponse = route(requestWithSession(DELETE, "/kindergarten/93740362")).get

        status(deleteResponse) must equalTo(UNAUTHORIZED)
      }

      "delete all teachers as well" in new WithApplication {

        val deleteResponse = route(requestByOperator(DELETE, "/kindergarten/93740362")).get

        status(deleteResponse) must equalTo(OK)

        val notFoundResponse = route(requestWithSession(GET, "/kindergarten/93740362/employee/13060003722")).get

        status(notFoundResponse) must equalTo(UNAUTHORIZED)
      }

      "be created with deleted login name" in new WithApplication {

        val deleteResponse = route(requestByOperator(DELETE, "/kindergarten/93740362/employee/13708089040")).get

        status(deleteResponse) must equalTo(OK)

        private val principalPhone: String = "23708089040"
        private val firstSchool: CreatingSchool = CreatingSchool(123, "010-88881111", "名称", "token",
          PrincipalOfSchool("e0002", "pass", principalPhone),
          ChargeInfo(123, 100, "2016-01-01", 1, 99), "address", Some("全名"))

        val createResponse = route(requestByOperator(POST, "/kindergarten").withBody(Json.toJson(firstSchool))).get
        status(createResponse) must equalTo(OK)

        val principalResponse = route(requestWithSession(GET, "/kindergarten/93740362/employee/23708089040")).get
        status(principalResponse) must equalTo(OK)

        val res: JsValue = Json.parse(contentAsString(principalResponse))
        (res \ "login_name").as[String] must equalTo("e0002")
        (res \ "phone").as[String] must equalTo("23708089040")
      }

      "add school config" in new WithApplication {

        val config = SchoolConfig(93740362, List(ConfigItem("key", "value")), List())
        val created = route(requestByOperator(POST, "/api/v2/school_config/93740362").withBody(Json.toJson(config))).get

        status(created) must equalTo(OK)

        val principalResponse = route(requestWithSession(GET, "/api/v2/school_config/93740362")).get
        status(principalResponse) must equalTo(OK)

        val res: JsValue = Json.parse(contentAsString(principalResponse))
        (res \ "config" ).as[List[ConfigItem]] must contain(ConfigItem("key", "value"))
        (res \ "school_customized" ).as[List[ConfigItem]] must beEmpty
      }

      "add school private config" in new WithApplication {

        val config = SchoolConfig(93740362, List(), List(ConfigItem("key", "value")))
        val created = route(requestByOperator(POST, "/api/v2/school_config/93740362").withBody(Json.toJson(config))).get

        status(created) must equalTo(OK)

        val principalResponse = route(requestWithSession(GET, "/api/v2/school_config/93740362")).get
        status(principalResponse) must equalTo(OK)

        val res: JsValue = Json.parse(contentAsString(principalResponse))
        (res \ "school_customized" ).as[List[ConfigItem]] must contain(ConfigItem("key", "value", SchoolConfig.SCHOOL_INDIVIDUAL_SETTING))
      }

      "override private config by global" in new WithApplication {
        val privateItem = ConfigItem("key", "privateItem")
        val globalItem = ConfigItem("key", "globalItem")

        //add private first
        val config1 = SchoolConfig(93740362, List(), List(privateItem))
        val created1 = route(requestByOperator(POST, "/api/v2/school_config/93740362").withBody(Json.toJson(config1))).get

        status(created1) must equalTo(OK)

        //then add global
        val config2 = SchoolConfig(93740362, List(globalItem), List())
        val created2 = route(requestByOperator(POST, "/api/v2/school_config/93740362").withBody(Json.toJson(config2))).get

        status(created2) must equalTo(OK)

        val principalResponse = route(requestWithSession(GET, "/api/v2/school_config/93740362")).get
        status(principalResponse) must equalTo(OK)

        val res: JsValue = Json.parse(contentAsString(principalResponse))
        (res \ "config" ).as[List[ConfigItem]] must contain(ConfigItem("key", "globalItem"))
        (res \ "school_customized" ).as[List[ConfigItem]] must beEmpty

      }

      "not allow to override global config by principals" in new WithApplication {
        val privateItem = ConfigItem("key", "privateItem")
        val globalItem = ConfigItem("key", "globalItem")

        //add global first
        val config2 = SchoolConfig(93740362, List(globalItem), List())
        val created2 = route(requestByOperator(POST, "/api/v2/school_config/93740362").withBody(Json.toJson(config2))).get

        status(created2) must equalTo(OK)

        //then try to update private item by principals
        val config1 = SchoolConfig(93740362, List(), List(privateItem))
        val created1 = route(requestWithSession(POST, "/api/v2/kindergarten/93740362/config").withBody(Json.toJson(config1))).get

        status(created1) must equalTo(OK)

        val principalResponse = route(requestWithSession(GET, "/api/v2/school_config/93740362")).get
        status(principalResponse) must equalTo(OK)

        val res: JsValue = Json.parse(contentAsString(principalResponse))
        (res \ "config" ).as[List[ConfigItem]] must contain(ConfigItem("key", "globalItem"))
        (res \ "school_customized" ).as[List[ConfigItem]] must beEmpty

      }

      "add school private config by principals" in new WithApplication {

        val config = SchoolConfig(93740362, List(), List(ConfigItem("key", "value")))
        val created = route(requestWithSession(POST, "/api/v2/kindergarten/93740362/config").withBody(Json.toJson(config))).get

        status(created) must equalTo(OK)

        val principalResponse = route(requestWithSession(GET, "/api/v2/school_config/93740362")).get
        status(principalResponse) must equalTo(OK)

        val res: JsValue = Json.parse(contentAsString(principalResponse))
        (res \ "school_customized" ).as[List[ConfigItem]] must contain(ConfigItem("key", "value", SchoolConfig.SCHOOL_INDIVIDUAL_SETTING))
      }

    }

  }
}
