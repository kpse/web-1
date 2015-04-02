package controllers.V2

import controllers.Secured
import models.json_models.{CheckChildInfo, CheckInfo}
import models.json_models.CheckingMessage._
import models.{Relationship, BusLocation, ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object BusLocationController extends Controller with Secured  {
  def index(kg: Long, driverId: String) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(BusLocation.index(kg, driverId)))
  }

  def create(kg: Long, driverId: String) = IsLoggedIn(parse.json) {
    u => request =>
      request.body.validate[BusLocation].map {
        case (location) =>
          BusLocation.create(kg, driverId, location)
          Ok(Json.toJson(new SuccessResponse))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def childOnBus(kg: Long, childId: String) = IsLoggedIn {
    u => _ =>
      BusLocation.child(kg, childId) match {
        case Some(x) if x.status == Some(2) =>
          NotFound(Json.toJson(ErrorResponse(s"今天早上${childId}已到校(Attended to school today.)", 2)))
        case Some(x) if x.status == Some(4) =>
          NotFound(Json.toJson(ErrorResponse(s"今天下午${childId}已到站下车(Got off the school bus already today.)", 4)))
        case Some(x) =>
          Ok(Json.toJson(x))
        case None =>
          NotFound(Json.toJson(ErrorResponse(s"今天没有${childId}的乘车记录(No riding records today.)")))
      }
      
  }

  def checkIn(kg: Long, driverId: String) = IsLoggedIn(parse.json) {
    u => request =>
      request.body.validate[CheckInfo].map {
        case (check) =>
          Relationship.getChildIdByCard(check.card_no) map (BusLocation.checkIn(kg, driverId, _, check.card_no))
          Ok(Json.toJson(new SuccessResponse))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def checkOut(kg: Long, driverId: String) = IsLoggedIn(parse.json) {
      u => request =>
        request.body.validate[CheckInfo].map {
          case (check) =>
            Relationship.getChildIdByCard(check.card_no) map (BusLocation.checkOut(kg, driverId, _))
            Ok(Json.toJson(new SuccessResponse))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }

  def childIn(kg: Long, driverId: String) = IsLoggedIn(parse.json) {
    u => request =>
      request.body.validate[CheckChildInfo].map {
        case (check) =>
          BusLocation.childrenOnBus(kg, driverId, check.child_id, "")
          Ok(Json.toJson(new SuccessResponse))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def childOut(kg: Long, driverId: String) = IsLoggedIn(parse.json) {
      u => request =>
        request.body.validate[CheckChildInfo].map {
          case (check) =>
            BusLocation.childrenOffBus(kg, driverId, check.child_id)
            Ok(Json.toJson(new SuccessResponse))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }

  def batchCheckOut(kg: Long, driverId: String) = IsLoggedIn(parse.json) {
    u => request =>
      request.body.validate[List[CheckInfo]].map {
        case (cards) =>
          cards map ((checkInfo:CheckInfo) => Relationship.getChildIdByCard(checkInfo.card_no) map (BusLocation.checkOut(kg, driverId, _)))
          Ok(Json.toJson(SuccessResponse(s"一共${cards.length}名学生下车(${cards.length} students get off the bus.)")))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }
}
