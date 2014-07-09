package models

import _root_.helper.TestSupport
import org.specs2.mutable.Specification
import play.cache.Cache

class VerificationSpec extends Specification with TestSupport {

  private val phone = "phone"
  private val code = "code"
  private val otherCode = "code2"

  "Verification" should {
    "compare code with cache" in new WithApplication {
      Cache.set(phone, code, 100)
      Verification.isMatched(Verification(phone, code)) must beTrue
      Cache.remove(phone)
    }

    "return false when no cache present" in new WithApplication {
      Verification.isMatched(Verification(phone, code)) must beFalse
      Cache.remove(phone)
    }

    "return false when cache not matched" in new WithApplication {
      Cache.set(phone, otherCode, 100)
      Verification.isMatched(Verification(phone, code)) must beFalse
      Cache.remove(phone)
    }

  }

}
