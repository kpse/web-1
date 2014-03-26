package models

import _root_.helper.TestSupport
import org.specs2.mutable.Specification
import models.json_models.{CheckInfo, CheckingMessage}

class ParentSpec extends Specification with TestSupport {

  "Parent" should {
    "report index" in new WithApplication {

      private val index = Parent.simpleIndex(93740362, None, None)

      index.size must greaterThan(5)
      index(0).parent_id must beSome("2_93740362_123")
      index(1).member_status must beSome(1)

    }

    "report parents in class" in new WithApplication {

      private val index = Parent.indexInClass(93740362, 777666, None)

      index.size must lessThan(5)
      index(0).parent_id must beSome("2_93740362_792")
      index(1).member_status must beSome(1)

    }

    "report parents who has no connection" in new WithApplication {

      private val index = Parent.simpleIndex(93740362, None, Some(false))

      index.size must lessThan(5)
      index(1).parent_id must beSome("2_93740362_999")
      index(0).member_status must beSome(1)
      index(1).member_status must beSome(1)

    }


    "create with phone inserted in push account" in new WithApplication {
      val newPhone = "12343212"
      private val parent = Parent.create(93740362, new Parent(None, 93740362, "", newPhone, None, 0, "1990-01-01", None, None, None))

      parent.nonEmpty must beTrue
      parent.map {
        p =>
          p.parent_id.nonEmpty must beTrue
          p.member_status must beSome(0)
          p.status must beSome(1)
      }
      val child = Children.create(93740362, new ChildInfo(None, "child", "nick", "2009-01-02", 0, None, 777666, None, None, None))
      private val card = "123"
      Relationship.create(93740362, card, "mama", newPhone, child.get.child_id.get)
      private val convert = CheckingMessage.convert(new CheckInfo(93740362, card, 2, 0, "", 0))

      convert.nonEmpty must beTrue
    }

    "update with phone updated in push account" in new WithApplication {
      val newPhone = "13402819999"
      private val parent = Parent.update(new Parent(Some("2_93740362_123"), 93740362, "", newPhone, None, 0, "1990-01-01", Some(0), Some(1), Some(1)))

      parent.nonEmpty must beTrue
      parent.map {
        p =>
          p.parent_id must beSome("2_93740362_123")
          p.member_status must beSome(1)
          p.status must beSome(1)
      }
      val child = Children.create(93740362, new ChildInfo(None, "child", "nick", "2009-01-02", 0, None, 777666, None, None, None))
      private val card = "123321"
      Relationship.create(93740362, card, "mama", newPhone, child.get.child_id.get)
      private val convert = CheckingMessage.convert(new CheckInfo(93740362, card, 2, 0, "", 0))

      convert.nonEmpty must beTrue
    }

    "throw exception when phone already exists in push account" in new WithApplication {
      val existsPhone = "13402815318"
      Parent.update(new Parent(Some("2_93740362_123"), 93740362, "", existsPhone, None, 0, "1990-01-01", Some(0), Some(1), Some(1))) must throwA(new IllegalAccessError("电话号码已经存在。"))
    }
  }
}
