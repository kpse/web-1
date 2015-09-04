package controllers.V3

import controllers.Secured
import models.{SuccessResponse, ErrorResponse}
import models.V3.Menu
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object DietMenuController extends Controller with Secured {

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(Menu.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Menu.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的菜单。(No such diet menu)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Menu].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) if s.nutrition_units.exists(_.id.nonEmpty) =>
        BadRequest(Json.toJson(ErrorResponse("新建的菜单不能包含已存在的营养单元信息(nutrition unit id is not allow in menu creating)", 4)))
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Menu].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) if s.deleted =>
        BadRequest(Json.toJson(ErrorResponse("该菜单已删除(given menu is deleted)", 5)))
      case (s) if !s.validUnits(kg) =>
        BadRequest(Json.toJson(ErrorResponse("菜单所包含的营养单元信息不正确(nutrition unit ids are not matched with this menu)", 6)))
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Menu.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
--膳食配餐菜单1
create table IMSInfo.dbo.DietBigMenuMaster(
SysNO int identity(1,1),系统编号
BigMenuName nvarchar(20) 存储名称
)

--膳食配餐菜单2
create table IMSInfo.dbo.DietBigMenuItem(
SysNO int identity(1,1),系统编号
BigMenuSysNO int,存储编号
MenuSysNO int,菜单编号
MenuName nvarchar(20),菜单名称
MenuWeight nvarchar(20),菜单重量
ArrangeType varchar(2)配餐类型
)
 */