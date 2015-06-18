package controllers.V3

import controllers.Secured
import models.{ErrorResponse, SuccessResponse}
import models.V3.Visitor
import models.json_models.CheckInfo
import models.json_models.CheckingMessage.checkInfoReads
import models.json_models.CheckingMessage.checkInfoWrites
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller



object VisitorController extends Controller with Secured {

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(Visitor.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Visitor.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的访客。(No such visitor)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Visitor].map {
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Visitor.deleteById(kg, id)
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
