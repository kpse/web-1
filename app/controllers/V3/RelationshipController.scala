package controllers.V3

import controllers.Secured
import models.Relationship
import play.api.libs.json.Json
import play.api.mvc.Controller

object RelationshipV3Controller extends Controller with Secured {

  def search(kg: Long, card: String) = IsLoggedIn {
    u => request =>
      val relationship: List[Relationship] = Relationship.cardInSchool(kg, card)
      Ok(Json.toJson(relationship))
  }
}
