package models

import _root_.helper.TestSupport
import org.specs2.mutable.Specification
import models.json_models.{MobileLogin, LoginCheck, CheckInfo, CheckingMessage}

class ParentSpec extends Specification with TestSupport {
  val kg: Long = 93740362
  "Parent" should {
    "report index" in new WithApplication {

      private val index = Parent.simpleIndex(93740362, None, None)

      index.size must greaterThan(5)
      index.head.parent_id.size must beGreaterThan(0)
      index(1).member_status must beSome(1)

    }

    "report parents in class" in new WithApplication {

      private val index = Parent.indexInClasses(93740362, "777666", None, None)

      index.size must lessThan(5)
      index.head.parent_id.size must beGreaterThan(0)
      index(1).member_status must beSome(1)

    }

    "report parents who has no connection" in new WithApplication {

      private val index = Parent.simpleIndex(93740362, None, Some(false))

      index.size must lessThan(5)
      index(1).parent_id.size must beGreaterThan(0)
      index.head.member_status must beSome(1)
      index(1).member_status must beSome(1)

    }

    "create with phone inserted in push account" in new WithApplication {
      val newPhone = "12343212"
      private val parent = Parent.create(kg, Parent(None, kg, "", newPhone, None, 0, "1990-01-01", None, None, None))

      parent.nonEmpty must beTrue
      parent.map {
        p =>
          p.parent_id.nonEmpty must beTrue
          p.member_status must beSome(1)
          p.status must beSome(1)
      }
      val child = Children.create(kg, ChildInfo(None, "child", "nick", None, 0, None, 777666, None, None, None))
      private val card = "123"
      Relationship.create(kg, card, "mama", newPhone, child.get.child_id.get)
      private val convert = CheckingMessage.convert(CheckInfo(kg, card, 2, 0, "", 0))

      convert.nonEmpty must beTrue
    }

    "update with phone updated in push account" in new WithApplication {
      val newPhone = "13402819999"
      private val parent = Parent.update(Parent(Some("2_93740362_123"), kg, "", newPhone, None, 0, "1990-01-01", Some(0), Some(1), Some(1)))

      parent.nonEmpty must beTrue
      parent.map {
        p =>
          p.parent_id must beSome("2_93740362_123")
          p.member_status must beSome(1)
          p.status must beSome(1)
      }
      val child = Children.create(kg, ChildInfo(None, "child", "nick", None, 0, None, 777666, None, None, None))
      private val card = "123321"
      Relationship.create(kg, card, "mama", newPhone, child.get.child_id.get)
      private val convert = CheckingMessage.convert(CheckInfo(kg, card, 2, 0, "", 0))

      convert.nonEmpty must beTrue
    }

    "throw exception when phone already exists in push account" in new WithApplication {
      val existingPhone = "13402815318"
      private val createdParent: Option[Parent] = Parent.create(93740362, Parent(Some("2_93740362_122223"), 93740362, "", existingPhone, None, 0, "1990-01-01", Some(0), Some(1), Some(1)))
      createdParent must beNone
    }

    "have default password as phone number" in new WithApplication {

      Parent.create(kg, createParent("11223344556"))

      private val result = LoginCheck(MobileLogin("11223344556", "23344556"))
      result.error_code must equalTo(0)
    }

    "be created despite of empty birthday" in new WithApplication {
      Parent.create(kg, createParent("11223344556").copy(birthday = ""))

      private val result = LoginCheck(MobileLogin("11223344556", "23344556"))
      result.error_code must equalTo(0)
    }


  }

  def createParent(phone: String) = Parent(None, kg, "name", phone, None, 0, "1980-01-01", None, None, None)
}
