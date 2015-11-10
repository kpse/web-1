package models

import _root_.helper.TestSupport
import org.specs2.matcher.Matcher
import org.specs2.mutable.Specification

class ReadNewsSpec extends Specification with TestSupport {

  "ReadNews" should {
    "report all readers of given news" in new WithApplication {

      private val index = ReadNews.allReaders(93740362L, 6)

      index.size must equalTo(3)
      index must contain { t: Option[Parent] => t must haveParentId("2_93740362_123") }
      index must contain { t: Option[Parent] => t must haveParentId("2_93740362_456") }
      index must contain { t: Option[Parent] => t must haveParentId("2_93740362_792") }
    }

    "reject not existing parent reading record" in new WithApplication {

      private val index = ReadNews.allReaders(93740362L, 9)

      index.size must equalTo(0)
    }
  }

  def haveParentId(id: String): Matcher[Option[Parent]] = { c: Option[Parent] =>
    (c.exists(_.parent_id.exists( _ == id)), s"$c doesn't contain the parent id $id")
  }

}
