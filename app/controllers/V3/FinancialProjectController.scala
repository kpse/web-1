package controllers.V3

import controllers.Secured
import models.{ErrorResponse, SuccessResponse}
import models.V3._
import models.V3.FinancialProject._
import models.V3.FinancialReceipt._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object FinancialProjectController extends Controller with Secured {

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(FinancialProject.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    FinancialProject.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的收费项目。(No such financial project)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[FinancialProject].map {
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[FinancialProject].map {
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    FinancialProject.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
* --收费母项目集合
create table IMSInfo.dbo.FinancialProjectParentEnum
(
SysNO int identity(1,1),
EnumName nvarchar(10),
Memo nvarchar(500)
)
默认值：保育费，食宿费，杂费

--收费项目
create table IMSInfo.dbo.FinancialProject(
SysNO int identity(1,1),系统编号
ParentSysNO int,母项目编号
Name nvarchar(20),项目名称
ShortName nvarchar(20),项目助记码
ID nvarchar(20),项目ID
Value nvarchar(20), 项目费用
Memo nvarchar(500)备注
)

--收费分组
create table IMSInfo.dbo.FinancialProjectGroup(
SysNO int identity(1,1),系统编号
Name nvarchar(20),分组名称
ShortName nvarchar(20),分组助记码
ID nvarchar(20)分组ID
)

--收费分组细节
create table IMSInfo.dbo.FinancialProjectGroupDetail(
SysNO int identity(1,1),系统编号
GroupSysNO int,分组系统编号
ProjectSysNO int项目编号
)

--收费打单
create table IMSInfo.dbo.FinancialReceipt(
SysNO int identity(1,1),系统编号
SerialNumber varchar(20),流水号
PayType int,收费类型
SNBase int,流水号基础值
Creator nvarchar(20),收费人
CreateTime datetime收费时间
)

--收费打单金额项
create table IMSInfo.dbo.FinancialReceiptItem(
SysNO int identity(1,1),系统编号
ReceiptSysNO int,单据系统编号
ProjectSysNO int,收费项目系统编号
ParentSysNO int,收费项目所属大项目编号
ParentName nvarchar(20),收费项目所属大项目名称
Name nvarchar(20),单行名称
ID nvarchar(20),单行ID
Value nvarchar(20), 单行金额
Memo nvarchar(500),备注
Reason nvarchar(500),修改理由
Count int,单行收费数量
SUMValue nvarchar(20)单行总价
)

--收费打单人员项（基本和学生基本数据一致，防止学生数据修改后影响原始单据内容）
create table IMSInfo.dbo.FinancialReceiptPerson(
SysNO int identity(1,1),系统编号
ReceiptSysNO int,收费单据系统编号
StudentSysNO int,学生系统编号
Name nvarchar(20),学生名称
DisplayName nvarchar(20),
FormerName nvarchar(20),
Gender int default(0),
StudentID nvarchar(50),
SocialID nvarchar(20),
ResidencePlace nvarchar(50),
ResidenceType varchar(10),
Nationality nvarchar(20),
OriginPlace nvarchar(20),
Ethnic nvarchar(20),
ClassSysNO int,
ClassName nvarchar(20),
GradeSysNO int,
GradeName nvarchar(20),
Birthday datetime,
StudentType int,
Address nvarchar(200),
InDate datetime,
Picture varchar(max),
Interest nvarchar(500),
BedNumber nvarchar(20),
Memo nvarchar(500)
)
*/

object FinancialProjectGroupController extends Controller with Secured {

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(FinancialProjectGroup.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    FinancialProjectGroup.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的收费组。(No such financial group)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[FinancialProjectGroup].map {
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[FinancialProjectGroup].map {
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    FinancialProjectGroup.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}

object FinancialReceiptController extends Controller with Secured {

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(FinancialReceipt.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    FinancialReceipt.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的发票。(No such receipt)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[FinancialReceipt].map {
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[FinancialReceipt].map {
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    FinancialReceipt.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}
