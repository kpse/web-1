package models

import _root_.helper.TestSupport
import org.specs2.mutable.Specification
import models.json_models.{CheckInfo, CheckingMessage}

class RelationshipSpec extends Specification with TestSupport {

  "Relationship" should {
    "report index" in new WithApplication {

      private val index = Relationship.index(93740362, None, None, None)

      index.size must greaterThan(5)
    }

    "report index by parent phone" in new WithApplication {

      private val index = Relationship.index(93740362, Some("13408654681"), None, None)

      index.size must beGreaterThan(1)
      index.head.parent.get.phone must equalTo("13408654681")
      index(1).parent.get.phone must equalTo("13408654681")

    }

    "report index by child id" in new WithApplication {

      private val index = Relationship.index(93740362, None, Some("1_93740362_374"), None)

      index.size must equalTo(2)
      index.head.child.get.child_id must beSome("1_93740362_374")
      index(1).child.get.child_id must beSome("1_93740362_374")

    }

    "report index by multiple children's id" in new WithApplication {

      private val index = Relationship.index(93740362, None, Some("1_93740362_374,1_93740362_456"), None)

      index.size must equalTo(3)
      index.head.child.get.child_id must beSome("1_93740362_456")
      index(1).child.get.child_id must beSome("1_93740362_374")
      index(2).child.get.child_id must beSome("1_93740362_374")

    }

    "report index within class" in new WithApplication {

      private val index = Relationship.index(93740362, None, None, Some(777999))

      index.size must beGreaterThan(1)
      index.head.child.get.class_id must equalTo(777999)
      index(1).child.get.class_id must equalTo(777999)

    }

    "be created with phone and childId" in new WithApplication {

      private val created = Relationship.create(93740362, "9", "妈妈", "13408654681", "1_1391836223533")

      private val newRelationship = created.get
      newRelationship.parent.get.phone must equalTo("13408654681")
      newRelationship.child.get.child_id must beSome("1_1391836223533")
    }

    "be updated by card info" in new WithApplication {

      private val created = Relationship.update(93740362, "0001234567", "妈妈", "13408654680", "1_1391836223533", 1)

      private val newRelationship = created.get
      newRelationship.parent.get.phone must equalTo("13408654680")
      newRelationship.child.get.child_id must beSome("1_1391836223533")

    }

    "be query by multiple child ids" in new WithApplication {

      private val query = Relationship.index(93740362, None, Some("1_93740362_374,1_93740362_456,1_93740362_778"), None)

      query.size must be greaterThanOrEqualTo(3)
    }

  }
}
