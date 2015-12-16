package models.V3

import _root_.helper.TestSupport
import org.specs2.mutable.Specification

class HardwareSpec extends Specification with TestSupport {

  "Hardware" should {
    "has memo field" in new WithApplication {
      private val hardware: Hardware = Hardware(None, Some("hard"), None, None, None, None, None, Some("memo"), None, None).create(93740362).get

      hardware.memo must beSome("memo")
    }

    "update memo field" in new WithApplication {
      private val hardware: Hardware = Hardware.show(93740362, 1).get

      hardware.copy(memo = Some("newMemo")).update(93740362)

      Hardware.show(93740362, 1).get.memo must beSome("newMemo")
    }

  }

}
