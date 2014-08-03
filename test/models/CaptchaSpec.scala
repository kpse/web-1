package models

import _root_.helper.TestSupport
import org.specs2.mutable.Specification

class CaptchaSpec extends Specification with TestSupport {

  "Captcha" should {
    "should accept challenge" in new WithApplication {

      private val result: Boolean = ReCaptcha.simpleCheck("e6f7681249d77e4e69c69fe7866f532b", "damage")

      result must beTrue

    }

    "should reject empty answer" in new WithApplication {

      private val result: Boolean = ReCaptcha.simpleCheck("", "")

      result must beFalse

    }

    "should reject empty question" in new WithApplication {

      private val result: Boolean = ReCaptcha.simpleCheck("", "damage")

      result must beFalse

    }

    "should reject wrong answer" in new WithApplication {

      private val result: Boolean = ReCaptcha.simpleCheck("fced89169747395b75103c3a613bbb50", "damage")

      result must beFalse

    }

  }
}
