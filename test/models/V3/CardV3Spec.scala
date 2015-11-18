package models.V3

import _root_.helper.TestSupport
import org.specs2.mutable.Specification

class CardV3Spec extends Specification with TestSupport {

  "Card" should {
    "check origin format is a 10 digits string" in {

      CardV3(None, "", "1234567890").wellFormatted must beTrue
      CardV3(None, "", "123456789").wellFormatted must beFalse
      CardV3(None, "", "12345678901").wellFormatted must beFalse
      CardV3(None, "", "").wellFormatted must beFalse
      CardV3(None, "", "a1234567789").wellFormatted must beFalse
      CardV3(None, "", "abc").wellFormatted must beFalse
    }

  }

}
