package models

import _root_.helper.TestSupport
import org.specs2.mutable.Specification

class TeacherSpec extends Specification with TestSupport {

  "Teacher" should {
    "should have unapply method for pattern matching" in new WithApplication {

      private val parent: Option[Employee] = Teacher.unapply("e0001")

      parent.get.id must beSome("3_93740362_1122")

    }

  }
}
