package models.json_models

import org.specs2.mutable.Specification
import helper.TestSupport

class CheckPhoneResponseSpec extends Specification with TestSupport {

  "CheckPhone" should {
    "response with 1102 when phone number is active" in new WithApplication {

      private val response = Check(CheckPhone("13279491366"))

      response.check_phone_result must equalTo("1102")
    }

    "response with 1100 when phone number does not exist in accountinfo" in new WithApplication {

      private val response = Check(CheckPhone("99999"))

      response.check_phone_result must equalTo("1100")
    }

    "response with 1101 when phone number is not a member" in new WithApplication {

      private val response = Check(CheckPhone("22222222222"))

      response.check_phone_result must equalTo("1101")
    }

    "response with 1101 when phone number has been soft deleted" in new WithApplication {

      private val response = Check(CheckPhone("22222222225"))

      response.check_phone_result must equalTo("1101")
    }

  }
}
