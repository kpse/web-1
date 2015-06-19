package controllers.V3

import controllers.Secured
import models.{ErrorResponse, SuccessResponse}
import models.V3.{Immune, ImmuneRecord}
import models.V3.ImmuneRecord._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object ImmuneRecordController extends Controller with Secured {

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(ImmuneRecord.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    ImmuneRecord.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的免疫记录。(No such immune record)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[ImmuneRecord].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[ImmuneRecord].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) if !s.exists(kg) =>
        BadRequest(Json.toJson(ErrorResponse(s"ID ${id} 不存在(id is not existing)", 4)))
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    ImmuneRecord.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
*
create table IMSInfo.dbo.ImmuneRecord(
SysNO int identity(1,1),系统编号
ImmuneSysNO int,疫苗编号，来自疫苗枚举
ImmuneName nvarchar(20),疫苗名称
ImmuneDescription nvarchar(500),描述
ImmuneSubSysNO int,针形编号
ImmuneSubName nvarchar(20),针形名称
Memo nvarchar(500),备注
ImmuneTime datetime接种时间
)

--疫苗种类枚举
create table IMSInfo.dbo.ImmuneEnum(
SysNO int identity(1,1),
EnumName nvarchar(10),
Memo nvarchar(500)
)
默认值：
'乙脑',
'一岁；一岁半至两岁'

'脊髓灰质炎活疫苗',
'一岁半至两岁；四岁'

'百白破制剂',
'一岁半至两岁'

'麻疹活疫',
'四岁'

*/
