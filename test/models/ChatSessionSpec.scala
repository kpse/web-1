package models

import _root_.helper.TestSupport
import org.specs2.mutable.Specification

class ChatSessionSpec extends Specification with TestSupport {

  "ChatSession" should {
    "report index and ordered by timestamp in desc" in new WithApplication {

      private val index = ChatSession.index(93740362L, "1_93740362_9982", None, None, Some(100))

      index.size must equalTo(39)
      index.head.topic must equalTo("1_93740362_9982")
      index.head.timestamp must greaterThan(index(2).timestamp)

    }

    "report index with from" in new WithApplication {

      private val index = ChatSession.index(93740362L, "1_93740362_9982", Some(1), None, Some(100))

      index.size must equalTo(39)
      index.head.topic must equalTo("1_93740362_9982")
      index.head.id must greaterThan(index(1).id)

    }

    "report index with to" in new WithApplication {

      private val index = ChatSession.index(93740362L, "1_93740362_9982", None, Some(4))

      index.size must equalTo(3)
      index.head.topic must equalTo("1_93740362_9982")
      index.head.id must equalTo(Some(4))
      index.head.id must greaterThan(index(1).id)

    }
    "report empty if from the end" in new WithApplication {

      private val index = ChatSession.index(93740362L, "1_93740362_9982", Some(999), None)

      index.size must equalTo(0)
    }

    "report beginning if to the beginning" in new WithApplication {

      private val index = ChatSession.index(93740362L, "1_93740362_9982", None, Some(2))

      index.size must equalTo(1)
    }
  }


}
