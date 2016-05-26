package models

import _root_.helper.TestSupport
import org.specs2.mutable.Specification

class AssessSpec extends Specification with TestSupport {

  "Assess" should {
    "report index ordered by timestamp by default" in new WithApplication {

      private val index = Assess.all(93740362L, "1_93740362_374", None, None)

      index.size must equalTo(2)
      index.head.child_id must equalTo("1_93740362_374")
      index.head.id must equalTo(Some(1))
      index.head.timestamp must greaterThan(index(1).timestamp)

    }

    "report index with from" in new WithApplication {

      private val index = Assess.all(93740362L, "1_93740362_374", Some(1), None)

      index.size must equalTo(1)
      index.head.child_id must equalTo("1_93740362_374")
      index.head.id must equalTo(Some(2))

    }

    "report index with to" in new WithApplication {

      private val index = Assess.all(93740362L, "1_93740362_374", None, Some(3))

      index.size must equalTo(2)
      index.head.child_id must equalTo("1_93740362_374")
      index.head.id must equalTo(Some(2))
      index.head.id must greaterThan(index(1).id)

    }
    "report empty if from the end" in new WithApplication {

      private val index = Assess.all(93740362L, "1_93740362_374", Some(3), None)

      index.size must equalTo(0)
    }

    "report empty if to the beginning" in new WithApplication {

      private val index = Assess.all(93740362L, "1_93740362_374", None, Some(1))

      index.size must equalTo(0)
    }
  }


}
