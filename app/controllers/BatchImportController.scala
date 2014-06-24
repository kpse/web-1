package controllers

import play.api.mvc.Controller
import models.{SuccessResponse, BatchImportReport, BatchDataImport, ImportedParent}
import play.api.libs.json.{JsError, Json}

object BatchImportController extends Controller with Secured {

  implicit val read = Json.reads[ImportedParent]
  implicit val write = Json.writes[BatchImportReport]
  implicit val write1 = Json.writes[SuccessResponse]

  def parents = OperatorPage(parse.json) {
    u => request =>
      request.body.validate[List[ImportedParent]].map {
        case (parents) =>
          val report: List[Option[BatchImportReport]] = BatchDataImport.createOrUpdate(parents).filter(_.isDefined)
          report match {
            case x::xs =>
              Ok(Json.toJson(report))
            case List() =>
              Ok(Json.toJson(new SuccessResponse))
          }

      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }
  def employees = OperatorPage(parse.json) {
    u => request => Ok
  }
  def children = OperatorPage(parse.json) {
    u => request => Ok
  }
  def relationships = OperatorPage(parse.json) {
    u => request => Ok
  }
}
