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

      index.size must equalTo(2)
      index.head.topic must equalTo("1_93740362_9982")
      index.head.id must equalTo(Some(3))
      index.head.id must greaterThan(index(1).id)

    }
    "report empty if from the end" in new WithApplication {

      private val index = ChatSession.index(93740362L, "1_93740362_9982", Some(999), None)

      index.size must equalTo(0)
    }

    "report empty if to the beginning" in new WithApplication {

      private val index = ChatSession.index(93740362L, "1_93740362_9982", None, Some(1))

      index.size must equalTo(0)
    }

    "collect conversation from specific class" in new WithApplication {

      private val index = ChatSession.lastMessageInClasses(93740362L, Some("777999"))

      index.size must equalTo(1)
    }

    "collect conversation from multiple classes" in new WithApplication {

      private val index = ChatSession.lastMessageInClasses(93740362L, Some("777999,777666"))

      index.size must equalTo(2)
    }

    "report history by employee id" in new WithApplication {

      private val index = ChatSession.employeeHistory(93740362L, "2_93740362_790", None, None, None, None)

      index.size must equalTo(6)
    }

    "report history with most" in new WithApplication {

      private val index = ChatSession.employeeHistory(93740362L, "2_93740362_790", None, None, Some(5), None)

      index.size must equalTo(5)
    }

    "report session history by employee id" in new WithApplication {

      private val index = ChatSession.employeeSessionHistory(93740362L, "2_93740362_790", None, None, None, None)

      index.size must equalTo(25)
    }

    "report session history with most" in new WithApplication {

      private val index = ChatSession.employeeSessionHistory(93740362L, "2_93740362_790", None, None, Some(10), None)

      index.size must equalTo(10)
    }


  }


}
