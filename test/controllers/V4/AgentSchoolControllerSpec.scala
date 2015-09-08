package controllers.V4

import helper.TestSupport
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class AgentSchoolControllerSpec extends Specification with TestSupport {
  def requestWithSession(method: String, url: String) = FakeRequest(method, url).withSession("id" -> "1", "username" -> "a0001")

  "AgentSchoolController" should {
    "report school active statistics" in new WithApplication {

      val summaryResponse = route(requestWithSession(GET, "/api/v4/agent/1/kindergarten/93740362/active")).get

      status(summaryResponse) must equalTo(OK)
      contentType(summaryResponse) must beSome.which(_ == "application/json")
      val response: JsValue = Json.parse(contentAsString(summaryResponse))
      (response \ "activated").as[Long] must equalTo(6)
      (response \ "all").as[Long] must equalTo(9)
      (response \ "member").as[Long] must equalTo(8)
      (response \ "video").as[Long] must equalTo(2)
      (response \ "check_in_out").as[Long] must equalTo(0)
      (response \ "children").as[Long] must equalTo(6)
      (response \ "school_id").as[Long] must equalTo(93740362L)
    }
  }

}
