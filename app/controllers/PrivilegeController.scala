package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import models.{Employee, Principal}

object PrivilegeController extends Controller with Secured {

  implicit val write = Json.writes[Principal]

  def index(kg: Long) = IsOperator {
    u => _ =>
      Ok(Json.toJson(Employee.allPrincipal(kg)))
  }
}
