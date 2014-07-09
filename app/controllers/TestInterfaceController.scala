package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import models.{ForceUpdateResponse, AppPackage}

object TestInterfaceController extends Controller {

  implicit val writes = Json.writes[ForceUpdateResponse]

  def forceUpdate = Action {
    Ok(Json.toJson(AppPackage.forceUpdate))
  }

}
