package controllers.V3

import controllers.Secured
import models.V3.{WorkShift, WorkShiftDate, WorkerInShift}
import models.V3.WorkShift._
import models.V3.WorkShiftDate._
import models.V3.WorkerInShift._
import models.{ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller


object WorkShiftController extends Controller with Secured {

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(WorkShift.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    WorkShift.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的班次。(No such work shift)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[WorkShift].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[WorkShift].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    WorkShift.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
* create table TrackingInfo.dbo.WorkCheckMaster(
SysNO int identity(1,1),系统编号
UserType int,考勤人员类型（1学生2家长3员工）
CheckName nvarchar(20),考勤项目名称
StartHour int,开始时间小时
StartMinute int,开始时间分钟
EndHour int,结束时间小时
EndMinute int,结束时间分钟
IsSameDay int开始结束时间是否在同一天
)

--考勤设置从表
create table TrackingInfo.dbo.WorkCheckItem(
SysNO int identity(1,1),系统编号
CheckSysNO int,所属考勤项目编号
CheckDate datetime,特殊考勤的日期
CheckStatus int本item的启用方式
)
*/


object WorkerInShiftController extends Controller with Secured {

  def index(kg: Long, shiftId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(WorkerInShift.index(kg, shiftId, from, to, most)))
  }

  def show(kg: Long, shiftId: Long, id: Long) = IsLoggedIn { u => _ =>
    WorkerInShift.show(kg, shiftId, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的用户。(No such worker in that shift)")))
    }
  }

  def create(kg: Long, shiftId: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[WorkerInShift].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) if s.base_id != Some(shiftId) =>
        BadRequest(Json.toJson(ErrorResponse("班次ID不匹配(shift id is not match)", 4)))
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, shiftId: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[WorkerInShift].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) if s.base_id != Some(shiftId) =>
        BadRequest(Json.toJson(ErrorResponse("班次ID不匹配(shift id is not match)", 4)))
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, shiftId: Long, id: Long) = IsLoggedIn { u => _ =>
    WorkerInShift.deleteById(kg, shiftId, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}

object WorkShiftDateController extends Controller with Secured {

  def index(kg: Long, shiftId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(WorkShiftDate.index(kg, shiftId, from, to, most)))
  }

  def show(kg: Long, shiftId: Long, id: Long) = IsLoggedIn { u => _ =>
    WorkShiftDate.show(kg, shiftId, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的班次时间。(No such work shift date)")))
    }
  }

  def create(kg: Long, shiftId: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[WorkShiftDate].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) if s.base_id != Some(shiftId) =>
        BadRequest(Json.toJson(ErrorResponse("班次ID不匹配(shift id is not match)", 4)))
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, shiftId: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[WorkShiftDate].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) if s.base_id != Some(shiftId) =>
        BadRequest(Json.toJson(ErrorResponse("班次ID不匹配(shift id is not match)", 4)))
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, shiftId: Long, id: Long) = IsLoggedIn { u => _ =>
    WorkShiftDate.deleteById(kg, shiftId, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}