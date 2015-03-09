package controllers

import play.api.mvc.{SimpleResult, Request, Controller}
import models._
import play.api.libs.json.{JsValue, JsError, Json}
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
  val p: (String) => (Request[JsValue]) => SimpleResult = {
    u => request =>
      request.body.validate[List[ImportedParent]].map {
        case (parents) =>
          Ok(report(parents.map(_.importing)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }


  val c: (String) => (Request[JsValue]) => SimpleResult = {
    u => request =>
      request.body.validate[List[ImportedChild]].map {
        case (children) =>
          Ok(report(children.map(_.importing)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  val e: (String) => (Request[JsValue]) => SimpleResult = {
    u => request =>
      request.body.validate[List[Employee]].map {
        case (employees) =>
          Ok(report(employees.map(_.importing)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  val r: (String) => (Request[JsValue]) => SimpleResult = {
    u => request =>
      request.body.validate[List[ImportedRelationship]].map {
        case (relationships) =>
          Ok(report(relationships.map(_.importing).filter(_.isDefined)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def parents = OperatorPage(parse.json)(p)

  def children = OperatorPage(parse.json)(c)

  def employees = OperatorPage(parse.json)(e)

  def relationships = OperatorPage(parse.json)(r)

  def parentsInSchool(kg: Long) = IsPrincipal(parse.json)(p)

  def childrenInSchool(kg: Long) = IsPrincipal(parse.json)(c)

  def employeesInSchool(kg: Long) = IsPrincipal(parse.json)(e)

  def relationshipsInSchool(kg: Long) = IsPrincipal(parse.json)(r)
}
