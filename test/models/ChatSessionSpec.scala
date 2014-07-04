package models

import _root_.helper.TestSupport
import org.specs2.mutable.Specification

class ChatSessionSpec extends Specification with TestSupport {

  "ChatSession" should {
    "report index and ordered by timestamp in desc" in new WithApplication {

      private val index = ChatSession.index(93740362L, "1_93740362_9982", None, None)

      index.size must equalTo(4)
      index(0).topic must equalTo("1_93740362_9982")
      index(0).id must equalTo(Some(11))
      index(0).timestamp must greaterThan(index(2).timestamp)

    }

    "report index with from" in new WithApplication {

      private val index = ChatSession.index(93740362L, "1_93740362_9982", Some(1), None)

      index.size must equalTo(4)
      index(0).topic must equalTo("1_93740362_9982")
      index(0).id must equalTo(Some(11))
      index(0).id must greaterThan(index(1).id)

    }

    "report index with to" in new WithApplication {

      private val index = ChatSession.index(93740362L, "1_93740362_9982", None, Some(4))

      index.size must equalTo(2)
      index(0).topic must equalTo("1_93740362_9982")
      index(0).id must equalTo(Some(3))
      index(0).id must greaterThan(index(1).id)

    }
    "report empty if from the end" in new WithApplication {

      private val index = ChatSession.index(93740362L, "1_93740362_9982", Some(11), None)

      index.size must equalTo(0)
    }

    "report empty if to the beginning" in new WithApplication {

      private val index = ChatSession.index(93740362L, "1_93740362_9982", None, Some(1))

      index.size must equalTo(0)
    }
  }


}
