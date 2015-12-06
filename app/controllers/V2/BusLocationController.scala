package controllers.V2

import controllers.PushController.createSwipeMessage
import controllers.Secured
import models.json_models.{CheckingMessage, CheckChildInfo, CheckInfo}
import models.json_models.CheckingMessage._
import models._
import play.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object BusLocationController extends Controller with Secured {
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
          val millis: Long = System.currentTimeMillis
          Relationship.getChildIdByCard(check.card_no) map (BusLocation.checkIn(kg, driverId, _, check.card_no, millis))
          pushToParents(check.copy(notice_type = 10, timestamp = millis))
          Ok(Json.toJson(new SuccessResponse))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def pushToParents(check: CheckInfo): Unit = {
    val messages = check.create
    Logger.info("bus location push messages : " + messages)
    messages map {
      m =>
        createSwipeMessage(m)
    }
  }

  def pushToParents2(check: CheckChildInfo): Unit = {
    val messages = check.create
    Logger.info("bus location push messages : " + messages)
    messages map {
      m =>
        createSwipeMessage(m)
    }
  }

  def checkOut(kg: Long, driverId: String) = IsLoggedIn(parse.json) {
    u => request =>
      request.body.validate[CheckInfo].map {
        case (check) =>
          val millis: Long = System.currentTimeMillis
          Relationship.getChildIdByCard(check.card_no) map (BusLocation.checkOut(kg, driverId, _, millis))
          pushToParents(check.copy(notice_type = 13, timestamp = millis))
          Ok(Json.toJson(new SuccessResponse))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def childIn(kg: Long, driverId: String) = IsLoggedIn(parse.json) {
    u => request =>
      request.body.validate[CheckChildInfo].map {
        case (check) =>
          val millis: Long = System.currentTimeMillis
          BusLocation.childrenOnBus(kg, driverId, check.child_id, "", millis)
          pushToParents2(check.copy(check_type = 12, timestamp = millis))
          Ok(Json.toJson(new SuccessResponse))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def childOut(kg: Long, driverId: String) = IsLoggedIn(parse.json) {
    u => request =>
      request.body.validate[CheckChildInfo].map {
        case (check) =>
          val millis: Long = System.currentTimeMillis
          BusLocation.childrenOffBus(kg, driverId, check.child_id, millis)
          pushToParents2(check.copy(check_type = 11, timestamp = millis))
          Ok(Json.toJson(new SuccessResponse))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def batchCheckOut(kg: Long, driverId: String) = IsLoggedIn(parse.json) {
    u => request =>
      request.body.validate[List[CheckChildInfo]].map {
        case (checks) =>
          checks foreach {
            (check: CheckChildInfo) =>
              val millis: Long = System.currentTimeMillis
              BusLocation.childrenOffBus(kg, driverId, check.child_id, millis)
              pushToParents2(check.copy(check_type = 11, timestamp = millis))
          }
          Ok(Json.toJson(SuccessResponse(s"今天早上一共${checks.length}名学生到校下车(${checks.length} students get off the bus.)")))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def batchCheckIn(kg: Long, driverId: String) = IsLoggedIn(parse.json) {
    u => request =>
      request.body.validate[List[CheckChildInfo]].map {
        case (checks) =>
          checks foreach {
            (check: CheckChildInfo) =>
              val millis: Long = System.currentTimeMillis
              BusLocation.childrenOnBus(kg, driverId, check.child_id, "", millis)
              pushToParents2(check.copy(check_type = 12, timestamp = millis))
          }
          Ok(Json.toJson(SuccessResponse(s"今天下午一共${checks.length}名学生离校上车(${checks.length} students get on the bus.)")))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def offlineBatchImport(kg: Long, driverId: String) = IsLoggedIn(parse.json) {
    u => request =>
      request.body.validate[List[CheckInfo]].map {
        case (checks) =>
          val result: List[CheckInfo] = checks map {
            case (check: CheckInfo) if check.notice_type == 10 =>
              Relationship.getChildIdByCard(check.card_no) foreach { childId =>
                BusLocation.checkIn(kg, driverId, childId, check.card_no, check.timestamp)
                pushToParents(check.copy(notice_type = 10))
              }
              check
            case (check: CheckInfo) if check.notice_type == 11 =>
              Relationship.getChildIdByCard(check.card_no) foreach { childId =>
                BusLocation.childrenOffBus(kg, driverId, childId, check.timestamp)
                pushToParents2(CheckChildInfo(kg, childId, 11, check.timestamp))
              }
              check
            case (check: CheckInfo) if check.notice_type == 12 =>
              Relationship.getChildIdByCard(check.card_no) foreach { childId =>
                BusLocation.childrenOnBus(kg, driverId, childId, check.card_no, check.timestamp)
                pushToParents2(CheckChildInfo(kg, childId, 12, check.timestamp))
              }
              check
            case (check: CheckInfo) if check.notice_type == 13 =>
              Relationship.getChildIdByCard(check.card_no) foreach  { childId =>
                BusLocation.checkOut(kg, driverId, childId, check.timestamp)
                pushToParents(check.copy(notice_type = 13))
              }
              check
          }
          Ok(Json.toJson(SuccessResponse(s"批量处理${result.length}名学生的校车事件(${result.length} students school bus events handled.)")))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }
}
