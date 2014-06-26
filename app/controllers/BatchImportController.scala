package controllers

import play.api.mvc.Controller
import models._
import play.api.libs.json.{JsError, Json}
import models.ImportedParent
import models.SuccessResponse
import models.BatchImportReport
import models.BatchImportReport.report

object BatchImportController extends Controller with Secured {

  implicit val read = Json.reads[ImportedParent]
  implicit val read1 = Json.reads[ImportedChild]
  implicit val read2 = Json.reads[Employee]
  implicit val read3 = Json.reads[IdItem]
  implicit val read4 = Json.reads[ImportedRelationship]
  implicit val write = Json.writes[BatchImportReport]
  implicit val write1 = Json.writes[SuccessResponse]

  def parents = OperatorPage(parse.json) {
    u => request =>
      request.body.validate[List[ImportedParent]].map {
        case (parents) =>
          Ok(report(parents.map(_.importing)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def children = OperatorPage(parse.json) {
    u => request =>
      request.body.validate[List[ImportedChild]].map {
        case (children) =>
          Ok(report(children.map(_.importing)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def employees = OperatorPage(parse.json) {
    u => request =>
      request.body.validate[List[Employee]].map {
        case (employees) =>
          Ok(report(employees.map(_.importing)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def relationships = OperatorPage(parse.json) {
    u => request =>
      request.body.validate[List[ImportedRelationship]].map {
        case (relationships) =>
          Ok(report(relationships.map(_.importing).filter(_.isDefined)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }
}
