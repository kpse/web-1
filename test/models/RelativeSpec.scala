package models

import _root_.helper.TestSupport
import org.specs2.mutable.Specification
import models.json_models.CheckInfo

class RelativeSpec extends Specification with TestSupport {

  "Relative" should {
    "should have unapply method for pattern matching" in new WithApplication {

      private val parent: Option[Parent] = Relative.unapply("13402815317")

      parent.get.parent_id must beSome("2_93740362_123")

    }

  }
}
