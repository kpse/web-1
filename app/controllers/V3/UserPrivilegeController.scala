package controllers.V3

import controllers.Secured
import models.SuccessResponse
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

case class UserPrivilege(id: Option[Long], user_id: Option[Long], privilege_id: Option[Long], memo: Option[String])

object UserPrivilegeController extends Controller with Secured {

  implicit val writeUserPrivilege = Json.writes[UserPrivilege]
  implicit val readUserPrivilege = Json.reads[UserPrivilege]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(UserPrivilege(Some(1), Some(1), Some(1), Some("权限备注")))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(UserPrivilege(Some(id), Some(1), Some(1), Some("权限备注"))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[UserPrivilege].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[UserPrivilege].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
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
