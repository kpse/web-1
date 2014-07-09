package models.json_models

import org.specs2.mutable.Specification
import helper.TestSupport

class MobileLoginResponseSpec extends Specification with TestSupport {

  "MobileLogin" should {
    "success when password is correct" in new WithApplication {

      private val response = LoginCheck(MobileLogin("13402815317", "1q2w3e"))

      response.error_code must equalTo(0)
      response.access_token must equalTo("1386425935574")
      response.username must equalTo("李毅")
      response.school_name must equalTo("第三军区幼儿园")
      response.account_name must equalTo("13402815317")
    }
  }
}
