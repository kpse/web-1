package controllers.V3

import helper.TestSupport
import models.V3.{EmployeeExt, EmployeeV3}
import models.V3.EmployeeV3.writeEmployeeV3
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
object EmployeeV3ControllerSpec extends Specification with TestSupport {
  def principalLoggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_1122", "username" -> "e0001")
  }

  "Employee" should {
    "check authentication first" in new WithApplication {
      val response = route(FakeRequest(GET, "/api/v3/kindergarten/123/employee/123")).get

      status(response) must equalTo(UNAUTHORIZED)

    }

    "reject creating new employee which exists in other school" in new WithApplication {

      val res = route(principalLoggedRequest(DELETE, "/api/v3/kindergarten/93740362/employee/142")).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")
      val response: JsValue = Json.parse(contentAsString(res))

      (response \ "error_code").as[Long] must equalTo(0)

      val employeeBody = Json.parse("{\"basic\":{\"id\":\"3_93740362_6112211\",\"name\":\"李毅老师\",\"phone\":\"13227882591\"," +
        "\"gender\":0,\"workgroup\":\"教师组\",\"workduty\":\"教师\",\"portrait\":\"\",\"birthday\":\"1986-06-04\",\"school_id\":0," +
        "\"login_name\":\"somenewlogin\",\"timestamp\":0,\"privilege_group\":\"teacher\",\"status\":1,\"created_at\":0}, " +
        "\"ext\": {\"display_name\": \"display_name\"}}")
      val res2 = route(principalLoggedRequest(POST, "/api/v3/kindergarten/93740362/employee").withBody(employeeBody)).get
      status(res2) must equalTo(INTERNAL_SERVER_ERROR)
      val response2: JsValue = Json.parse(contentAsString(res2))

      (response2 \ "error_code").as[Long] must equalTo(6)
    }

    "reject updating one employee with a number which exists in other school" in new WithApplication {

      val employeeBody = Json.parse("{\"id\":142, \"basic\":{\"id\":\"3_93740362_6112211\",\"name\":\"李毅老师\",\"phone\":\"13227882591\"," +
        "\"gender\":0,\"workgroup\":\"教师组\",\"workduty\":\"教师\",\"portrait\":\"\",\"birthday\":\"1986-06-04\",\"school_id\":0," +
        "\"login_name\":\"somenewlogin\",\"timestamp\":0,\"privilege_group\":\"teacher\",\"status\":1,\"created_at\":0, \"uid\":142}, " +
        "\"ext\": {\"display_name\": \"display_name\"}}")
      val res2 = route(principalLoggedRequest(POST, "/api/v3/kindergarten/93740362/employee/142").withBody(employeeBody)).get
      status(res2) must equalTo(INTERNAL_SERVER_ERROR)
      val response2: JsValue = Json.parse(contentAsString(res2))

      (response2 \ "error_code").as[Long] must equalTo(6)
    }

    "ignore the school id from updating json" in new WithApplication {
      val res1 = route(principalLoggedRequest(GET, "/api/v3/kindergarten/93740362/employee/142")).get
      val employee142: JsValue = Json.parse(contentAsString(res1))
      private val employeeInfo: EmployeeV3 = employee142.as[EmployeeV3]
      val employeeWithWrongSchoolId = Json.toJson(employeeInfo.copy(basic = employeeInfo.basic.copy(school_id = 9999),
        ext = Some(EmployeeExt(None, None, None, None, None, None, None, None, None, None, None, None, None, None,None, None, None, None))))
      Logger.info("employeeWithWrongSchoolId: " + employeeWithWrongSchoolId.toString)
      val res2 = route(principalLoggedRequest(POST, "/api/v3/kindergarten/93740362/employee/142").withBody(employeeWithWrongSchoolId)).get
      status(res2) must equalTo(OK)
      val response2: JsValue = Json.parse(contentAsString(res2))

      (response2 \ "id").as[Long] must equalTo(142)
      (response2 \ "basic" \"school_id").as[Long] must equalTo(93740362)
    }

    "ignore the school id from creating json" in new WithApplication {
      val employeeBody = Json.parse("{\"basic\":{\"id\":\"3_93740362_6112299\",\"name\":\"李毅老师\",\"phone\":\"13402815318\"," +
        "\"gender\":0,\"workgroup\":\"教师组\",\"workduty\":\"教师\",\"portrait\":\"\",\"birthday\":\"1986-06-04\",\"school_id\":9999," +
        "\"login_name\":\"somenewlo8gin\",\"timestamp\":0,\"privilege_group\":\"teacher\",\"status\":1,\"created_at\":0}, " +
        "\"ext\": {\"display_name\": \"display_name\"}}")
      val res2 = route(principalLoggedRequest(POST, "/api/v3/kindergarten/93740362/employee").withBody(employeeBody)).get
      status(res2) must equalTo(OK)
      val response2: JsValue = Json.parse(contentAsString(res2))

      (response2 \ "id").as[Long] must greaterThan(142L)
      (response2 \ "basic" \ "school_id").as[Long] must equalTo(93740362)

    }

    "check login name conflict before creating" in new WithApplication {
      val employeeBody = Json.parse("{\"basic\":{\"name\":\"李毅老师\",\"phone\":\"13402815318\"," +
        "\"gender\":0,\"workgroup\":\"教师组\",\"workduty\":\"教师\",\"portrait\":\"\",\"birthday\":\"1986-06-04\",\"school_id\":9999," +
        "\"login_name\":\"e0001\",\"timestamp\":0,\"privilege_group\":\"teacher\",\"status\":1,\"created_at\":0}, " +
        "\"ext\": {\"display_name\": \"display_name\"}}")
      val res2 = route(principalLoggedRequest(POST, "/api/v3/kindergarten/93740362/employee").withBody(employeeBody)).get

      status(res2) must equalTo(BAD_REQUEST)
      val response2: JsValue = Json.parse(contentAsString(res2))

      (response2 \ "error_code").as[Long] must equalTo(10)

    }

    "ignore deleted login name conflict before creating" in new WithApplication {
      val res = route(principalLoggedRequest(DELETE, "/api/v3/kindergarten/93740362/employee/1")).get

      status(res) must equalTo(OK)

      val employeeBody = Json.parse("{\"basic\":{\"name\":\"李毅老师\",\"phone\":\"13402815318\"," +
        "\"gender\":0,\"workgroup\":\"教师组\",\"workduty\":\"教师\",\"portrait\":\"\",\"birthday\":\"1986-06-04\",\"school_id\":9999," +
        "\"login_name\":\"e0001\",\"timestamp\":0,\"privilege_group\":\"teacher\",\"status\":1,\"created_at\":0}, " +
        "\"ext\": {\"display_name\": \"display_name\"}}")
      val res2 = route(principalLoggedRequest(POST, "/api/v3/kindergarten/93740362/employee").withBody(employeeBody)).get

      status(res2) must equalTo(OK)
      val response2: JsValue = Json.parse(contentAsString(res2))

      (response2 \ "basic" \ "phone").as[String] must equalTo("13402815318")

    }

  }
}
