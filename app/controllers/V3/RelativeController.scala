package controllers.V3

import controllers.Secured
import models.{Parent, ChildInfo, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller
import models.Children.readChildInfo
import models.Children.writeChildInfo

case class ParentExt(display_name: Option[String], social_id: Option[String], nationality: Option[String], fix_line: Option[String], memo: Option[String])

case class Relative(id: Option[Long], basic: Parent, ext: ParentExt)

object RelativeController extends Controller with Secured {

  implicit val writeParentExt = Json.writes[ParentExt]
  implicit val readParentExt = Json.reads[ParentExt]

  implicit val writeRelative = Json.writes[Relative]
  implicit val readRelative = Json.reads[Relative]

  def info(kg: Long, id: Long): Parent = Parent(Some(s"2_${kg}_${id}"), kg, "老王", "13991855476",
    Some("http://suoqin-test.u.qiniudn.com/FhdoadN7g_dk3CZBaKi2Q-yG6hEI"), 1, "1979-01-01", Some(1427817610000L),
    Some(1), Some(1), Some("可口可乐中国办事处"), Some(1417817610000L))

  val ext: ParentExt = ParentExt(Some("大显示名"), Some("510122197801010274"), Some("中国"), Some("028-88884444"), Some("这家伙很懒，什么也没留下。"))

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(Relative(Some(1), info(kg, 1), ext))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(Relative(Some(id), info(kg, id), ext)))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Relative].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Relative].map {
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