package controllers.V3

import controllers.Secured
import models.SuccessResponse
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

case class Hardware(id: Option[Long], name: Option[String], ip: Option[String], port: Option[Int])

object HardwareController extends Controller with Secured {

  implicit val writeHardware = Json.writes[Hardware]
  implicit val readHardware = Json.reads[Hardware]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(Hardware(Some(1), Some("门口机"), Some("192.168.0.1"), Some(8080)))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(Hardware(Some(id), Some("门口机"), Some("192.168.0.1"), Some(8080))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Hardware].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Hardware].map {
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
