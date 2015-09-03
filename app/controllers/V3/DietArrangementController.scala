package controllers.V3

import controllers.Secured
import models.{SuccessResponse, ErrorResponse}
import models.V3.DietArrangement
import play.api.libs.json.{Json, JsError}
import play.api.mvc.Controller

object DietArrangementController extends Controller with Secured {

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(DietArrangement.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    DietArrangement.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的膳食配餐。(No such diet arrangement)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[DietArrangement].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(kg, 0)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[DietArrangement].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(kg, 0)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    DietArrangement.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*

--膳食配餐1
create table IMSInfo.dbo.DietArrangeMaster(
SysNO int identity(1,1),系统编号
MenuSysNO int,菜单编号
MenuName nvarchar(20),菜单名称
ArrangeType char(1),配餐类型，早餐午餐晚餐
ArrangeDate datetime配餐日期
)

--膳食配餐2
create table IMSInfo.dbo.DietArrangeItem(
SysNO int identity(1,1),系统编号
MasterSysNO int,所属配餐编号
GradeSysNO int,等级编号
Weight nvarchar(20)重量
)

*/