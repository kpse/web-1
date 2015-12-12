package models.V3

import _root_.helper.TestSupport
import org.specs2.mutable.Specification
import play.Logger

class StockingSpec extends Specification with TestSupport {

  "Stocking" should {
    "add items with employee info in updating" in new WithApplication {
      val employeeId: Long = 998
      val employeeName: String = "employeeName"

      val exists: List[Stocking] = Stocking.index(93740362, 1, None, None, Some(1))
      val newStocking: Stocking = exists.head.copy(items = Some(exists.head.items.get ::: List(StockingDetail(None, exists.head.id, Some(1), None, Some(employeeId), Some(employeeName), None, None, None, None, None, None, None))))

      val updated: Stocking = newStocking.update(93740362, 1).get

      updated.items.get.size must equalTo(exists.head.items.get.size + 1)

      val DataFromDb: Stocking = Stocking.show(93740362, 1, updated.id.get).get
      DataFromDb.items.get.size must equalTo(updated.items.get.size)
      DataFromDb.items.get.last.employee_id must beSome(employeeId)
      DataFromDb.items.get.last.employee_name must beSome(employeeName)
    }

  }

}
