package controllers.V3

import controllers.Secured
import models.SuccessResponse
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

case class Immune(id: Option[Long], name: Option[String], memo: Option[String])

case class ImmuneRecord(id: Option[Long], immune: Option[Immune], name: Option[String], description: Option[String], sub_id: Option[Long], sub_name: Option[String], memo: Option[String], created_at: Option[Long])

object ImmuneRecordController extends Controller with Secured {

  implicit val writeImmune = Json.writes[Immune]
  implicit val readImmune = Json.reads[Immune]
  implicit val writeImmuneRecord = Json.writes[ImmuneRecord]
  implicit val readImmuneRecord = Json.reads[ImmuneRecord]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(ImmuneRecord(Some(1), Some(Immune(Some(1), Some("乙脑"), Some("一岁；一岁半至两岁"))), Some("疫苗名字"), Some("疫苗描述"), Some(1), Some("子疫苗名字"), Some("一岁；一岁半至两岁"), Some(System.currentTimeMillis)))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(ImmuneRecord(Some(id), Some(Immune(Some(1), Some("乙脑"), Some("一岁；一岁半至两岁"))), Some("疫苗名字"), Some("疫苗描述"), Some(1), Some("子疫苗名字"), Some("一岁；一岁半至两岁"), Some(System.currentTimeMillis))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[ImmuneRecord].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[ImmuneRecord].map {
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
