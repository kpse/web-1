package controllers.V3

import controllers.Secured
import controllers.V3.CardController._
import models.{ErrorResponse, SuccessResponse}
import models.V3.{CardV3, Hardware}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object HardwareController extends Controller with Secured {

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(Hardware.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Hardware.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的设备。(No such hardware)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Hardware].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Hardware].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Hardware.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
* CREATE TABLE BaseInfo.[dbo].[Type_A_MachineInfo](
	[SysNO] [int] IDENTITY(1,1) NOT NULL, 系统编号
	[Name] [nvarchar](20) NULL, 机器名称
	[IP] [varchar](20) NULL,机器ip
	[Port] [varchar](10) NULL,机器端口
	CameraChannelNO int（摄像头编号，可弃）
)

--接送机信息
create table BaseInfo.dbo.CardMachineInfo(
SysNO int identity(1,1),系统编号
Name nvarchar(20),机器名称
IP varchar(20),ip地址
Port varchar(10),端口
Status int default(1),状态，可弃
CameraChannelNO int摄像头，可弃
)

*/
