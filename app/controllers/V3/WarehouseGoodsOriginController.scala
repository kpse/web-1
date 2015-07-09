package controllers.V3

import controllers.Secured
import models.V3.GoodsOrigin
import models.{ErrorResponse, ChildInfo, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object WarehouseGoodsOriginController extends Controller with Secured {

  def index(kg: Long, warehouseId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(GoodsOrigin.index(kg, warehouseId, from, to, most)))
  }

  def show(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn { u => _ =>
    GoodsOrigin.show(kg, warehouseId, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的产地。(No such goods origin)")))
    }
  }

  def create(kg: Long, warehouseId: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[GoodsOrigin].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(kg, warehouseId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[GoodsOrigin].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(kg, warehouseId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn { u => _ =>
    GoodsOrigin.deleteById(kg, warehouseId, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
*

--货物来源
create table IMSInfo.dbo.GoodsOrigin(
SysNO int identity(1,1),系统编号
Name nvarchar(50),货物来源名称
ShortName nvarchar(20),助记码
WarehouseSysNO int,仓库编号
Memo nvarchar(500)备注
)

*/
