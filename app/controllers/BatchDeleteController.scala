package controllers

import play.api.mvc.Controller
import models._
import play.api.libs.json.{JsError, Json}
import models.SuccessResponse
import models.BatchImportReport
import models.BatchImportReport.report


object BatchDeleteController extends Controller with Secured {

  implicit val read3 = Json.reads[IdItem]


  def deleteParents = OperatorPage(parse.json) {
    u => request =>
      request.body.validate[List[IdItem]].map {
        case (parents) =>
          Ok(report(parents.map(item => Parent.permanentRemoveById(item.id))))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }
}
