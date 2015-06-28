package controllers.V3

import controllers.Secured
import models.{ErrorResponse, SuccessResponse}
import models.V3.UserPrivilege
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object UserPrivilegeController extends Controller with Secured {

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(UserPrivilege.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    UserPrivilege.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的用户权限。(No such user privilege)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[UserPrivilege].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[UserPrivilege].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    UserPrivilege.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
*
create table BaseInfo.dbo.UserPrivilege(
SysNO int identity(1,1),系统编号
UserSysNO int,用户系统编号
PrivilegeSysNO int,权限系统编号
Memo nvarchar(50)备注
)

*/
