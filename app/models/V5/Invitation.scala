package models.V5

import models.helper.MD5Helper.md5
import models.{Verification, Parent, Relationship}
import play.Logger
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class NewParent(phone: String, name: String, relationship: String)

case class Invitation(from: Parent, to: NewParent, code: Verification) {
  def settle: List[Relationship] = DB.withTransaction {
    implicit c =>
      try {
        Parent.create(from.school_id, from.copy(parent_id = None, name = to.name, phone = to.phone))
        Relationship.index(from.school_id, Some(from.phone), None, None).foreach {
          r =>
            val childId: String = r.child.get.child_id.get
            val fakeCardNumber: String = "f" + md5(s"${to.phone}_$childId").take(19)
            Relationship.create(from.school_id, fakeCardNumber, to.relationship, to.phone, childId)
        }
        c.commit()
        Relationship.index(from.school_id, Some(to.phone), None, None)
      }
      catch {
        case e: Throwable => c.rollback()
          Logger.warn(e.getLocalizedMessage)
          List()
      }
  }
}

object Invitation {
  implicit val readNewParent = Json.reads[NewParent]
  implicit val writeNewParent = Json.writes[NewParent]
  implicit val readVerification = Json.reads[Verification]
  implicit val writeVerification = Json.writes[Verification]
  implicit val readInvitation = Json.reads[Invitation]
  implicit val writeInvitation = Json.writes[Invitation]


}
