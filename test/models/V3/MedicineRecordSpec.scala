package models.V3

import _root_.helper.TestSupport
import org.specs2.mutable.Specification

class MedicineRecordSpec extends Specification with TestSupport {

  "MedicineRecord" should {
    "have clocks by default" in new WithApplication {

      private val record = MedicineRecord.show(93740362, 1).get

      record.clocks must beEmpty
    }

    "be created with clocks" in new WithApplication {
      private val newCreated = MedicineRecord(None, 1, "", "", None, List(MedicineRecordReminder(1))).create(93740362).get

      private val record = MedicineRecord.show(93740362, newCreated.id.get).get

      record.clocks.head must equalTo(MedicineRecordReminder(1))
    }

    "be updated with clocks" in new WithApplication {
      private val updated = MedicineRecord(Some(1), 1, "title", "content", None, List(MedicineRecordReminder(2))).update(93740362).get

      private val record = MedicineRecord.show(93740362, updated.id.get).get

      record.clocks.head must equalTo(MedicineRecordReminder(2))
    }


  }

}
