package controllers.V3

import controllers.Secured
import models.SuccessResponse
import models.json_models.CheckInfo
import models.json_models.CheckingMessage.checkInfoReads
import models.json_models.CheckingMessage.checkInfoWrites
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

case class Visitor(id: Option[Long], name: Option[String], certification_type: Option[String], certification_number: Option[String], reason: Option[String],
                     time: Option[Long], quantity: Option[Int], visitor_user_id: Option[Long], visitor_user_type: Option[Int],
                      memo: Option[String], photo_record: Option[String], SGID_picture: Option[String])

object VisitorController extends Controller with Secured {

  implicit val writeVisitor = Json.writes[Visitor]
  implicit val readVisitor = Json.reads[Visitor]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(Visitor(Some(1), Some("老宋"), Some("身份证"), Some("510122198802282235"), Some("系统测试"), Some(System.currentTimeMillis), Some(3), Some(1), Some(1), Some("没有备注"), Some("https://dn-cocobabys.qbox.me/big_shots.jpg"), Some("https://dn-cocobabys.qbox.me/big_shots.jpg")))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(Visitor(Some(id), Some("老宋"), Some("身份证"), Some("510122198802282235"), Some("系统测试"), Some(System.currentTimeMillis), Some(3), Some(1), Some(1), Some("没有备注"), Some("https://dn-cocobabys.qbox.me/big_shots.jpg"), Some("https://dn-cocobabys.qbox.me/big_shots.jpg"))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Visitor].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Visitor].map {
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
* create table BaseInfo.dbo.VisitorInfo(
SysNO int identity(1,1),系统编号
Name nvarchar(20),访客姓名
CertificateType nvarchar(10),证件类型
CertificateNumber nvarchar(30),证件编号
VisitReason nvarchar(30),访问理由
VisitTime datetime,访问时间
VisitQTY int,访客人数
VisitUserSysNO int,被访问者系统编号
VisitUserType int,被访问者类型
Memo nvarchar(500),备注
PicPath varchar(max),访客照片
SGIDPicture varchar(max)身份证照片
)

*/
