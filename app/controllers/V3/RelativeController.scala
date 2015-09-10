package controllers.V3

import controllers.{RelationshipController, Secured}
import models.V3.Relative
import models.{Relationship, ErrorResponse, SuccessResponse}
import play.api.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller
import controllers.helper.CacheHelper._

object RelativeController extends Controller with Secured {

  implicit val RelativesCacheKey = "index_v3_relatives"
  createKeyCache

  def clearCurrentCache() = {
    clearAllCache
    RelationshipController.clearCurrentCache()
  }


  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    val cacheKey: String = s"Relatives_${kg}_${from}_${to}_${most}"
    Logger.info(s"RelativeController entering index = ${cacheKey}")

    val value: List[Relative] = digFromCache[List[Relative]](cacheKey, 600, () => {
      Relative.index(kg, from, to, most)
    })
    Ok(Json.toJson(value))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Relative.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的家长。(No such relative)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    Logger.info(s"relative create: ${request.body}")
    request.body.validate[Relative].map {
      case (s) if s.ext.isEmpty =>
        BadRequest(Json.toJson(ErrorResponse("必须提供完整的信息。(no ext part)", 2)))
      case (s) if s.basic.id.nonEmpty || s.id.nonEmpty =>
        BadRequest(Json.toJson(ErrorResponse("有id的情况请用update接口。(use update when you have ID value)", 4)))
      case (s) if s.basic.duplicatedPhoneWithOthers =>
        BadRequest(Json.toJson(ErrorResponse("手机与现有未删除家长重复，请先删除再创建。(duplicated phone number with another parent)", 6)))
      case (s) =>
        Relative.removeDirtyDataIfExists(s)
        clearCurrentCache()
        Ok(Json.toJson(s.create))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    Logger.info(s"relative update: ${request.body}")
    request.body.validate[Relative].map {
      case (s) if s.ext.isEmpty =>
        BadRequest(Json.toJson(ErrorResponse("必须提供完整的信息。(no ext part)", 2)))
      case (s) if s.id != s.basic.id  =>
        BadRequest(Json.toJson(ErrorResponse("内外id不一致。(ids should be consistent)", 3)))
      case (s) if s.id.isEmpty  =>
        BadRequest(Json.toJson(ErrorResponse("没有id无法更新。(no id for update)", 5)))
      case (s) =>
        clearCurrentCache()
        Ok(Json.toJson(s.update))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Relative.show(kg, id) match {
      case Some(x) =>
        Relative.deleteById(kg, id)
        x.basic.parent_id foreach Relationship.deleteCardByParentId
        clearCurrentCache()
        Ok(Json.toJson(new SuccessResponse()))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的父母。(No such relative)")))
    }
  }
}

/*
* create table IMSInfo.dbo.RelativeInfo(
SysNO int identity(1,1), 系统自增编号
Name nvarchar(20),  姓名
DisplayName nvarchar(20), 显示名称
Gender int default(0), 性别
SocialID nvarchar(20), 身份证号
Nationality nvarchar(20), 国籍
RelationShipSysNO int, 关系（有关系表可自定义关系的名称（姥姥小姨等）
MobilePhone varchar(20),手机
TelePhone varchar(20),座机
Company nvarchar(100),公司
Picture varchar(max),照片
Memo nvarchar(500),备注
SMValid int default(0)是否自动发送短信（无用了）
)

*/