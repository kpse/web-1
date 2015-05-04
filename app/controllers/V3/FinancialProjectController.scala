package controllers.V3

import controllers.Secured
import models.{ChildInfo, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

case class FinancialProject(id: Option[Long], parent_id: Option[Long], name: Option[String], short_name: Option[String], project_id: Option[String], total: Option[String], memo: Option[String])
case class FinancialProjectGroup(id: Option[Long], name: Option[String], short_name: Option[String], group_id: Option[String], projects: Option[List[FinancialProject]])

case class FinancialReceipt(id: Option[Long], serial_number: Option[String], payment_type: Option[String], sn_base: Option[Int], creator: Option[String], created_at: Option[Long])
case class FinancialReceiptItem(id: Option[Long], receipt: Option[FinancialReceipt], project: Option[FinancialProject], parent_id: Option[Long],
                                parent_name: Option[String], name: Option[String], row_id: Option[String], row_value: Option[String],
                                memo: Option[String], reason: Option[String], count: Option[Int], subtotal: Option[String], created_at: Option[Long])

case class FinancialRecipient(id: Option[Long], receipt: FinancialReceipt, student: Student)

object FinancialProjectController extends Controller with Secured {

  implicit val writeFinancialProject = Json.writes[FinancialProject]
  implicit val readFinancialProject = Json.reads[FinancialProject]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(FinancialProject(Some(1), Some(2), Some("1980年全年学费"), Some("学费"), Some("1"), Some("2000"), Some("记点什么")))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(FinancialProject(Some(id), Some(2), Some("1980年全年学费"), Some("学费"), Some("1"), Some("2000"), Some("记点什么"))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[FinancialProject].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[FinancialProject].map {
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

  import controllers.V3.FinancialProjectController.writeFinancialProject
  import controllers.V3.FinancialProjectController.readFinancialProject
  implicit val writeFinancialProjectGroup = Json.writes[FinancialProjectGroup]
  implicit val readFinancialProjectGroup = Json.reads[FinancialProjectGroup]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(FinancialProjectGroup(Some(1), Some("第一组"), Some("1组"), Some("1-2-3"), Some(List(FinancialProject(Some(1), Some(2), Some("1980年全年学费"), Some("学费"), Some("1"), Some("2000"), Some("记点什么"))))))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(FinancialProjectGroup(Some(1), Some("第一组"), Some("1组"), Some("1-2-3"), Some(List(FinancialProject(Some(1), Some(2), Some("1980年全年学费"), Some("学费"), Some("1"), Some("2000"), Some("记点什么")))))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[FinancialProjectGroup].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[FinancialProjectGroup].map {
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

object FinancialReceiptController extends Controller with Secured {

  implicit val writeFinancialReceipt = Json.writes[FinancialReceipt]
  implicit val readFinancialReceipt = Json.reads[FinancialReceipt]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(FinancialReceipt(Some(1), Some("123131312312"), Some("刷卡"), Some(321), Some(s"3_${kg}_1"), Some(System.currentTimeMillis)))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(FinancialReceipt(Some(id), Some("123131312312"), Some("刷卡"), Some(321), Some(s"3_${kg}_1"), Some(System.currentTimeMillis))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[FinancialReceipt].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[FinancialReceipt].map {
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

object FinancialReceiptItemController extends Controller with Secured {

  import controllers.V3.FinancialProjectController.writeFinancialProject
  import controllers.V3.FinancialProjectController.readFinancialProject
  import controllers.V3.FinancialReceiptController.writeFinancialReceipt
  import controllers.V3.FinancialReceiptController.readFinancialReceipt
  implicit val writeFinancialReceiptItem = Json.writes[FinancialReceiptItem]
  implicit val readFinancialReceiptItem = Json.reads[FinancialReceiptItem]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(FinancialReceiptItem(Some(1), Some(FinancialReceipt(Some(1), Some("123131312312"), Some("刷卡"), Some(321), Some(s"3_${kg}_1"), Some(System.currentTimeMillis))),
      Some(FinancialProject(Some(1), Some(2), Some("1980年全年学费"), Some("学费"), Some("1"), Some("2000"), Some("记点什么"))),
      Some(2), Some("父项目"), Some("子项目"), Some("1-1"), Some("250$"), Some("没交哈"), Some("本来说的今天交的"), Some(2), Some("251$"), Some(System.currentTimeMillis)))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(FinancialReceiptItem(Some(id), Some(FinancialReceipt(Some(1), Some("123131312312"), Some("刷卡"), Some(321), Some(s"3_${kg}_1"), Some(System.currentTimeMillis))),
      Some(FinancialProject(Some(1), Some(2), Some("1980年全年学费"), Some("学费"), Some("1"), Some("2000"), Some("记点什么"))),
      Some(2), Some("父项目"), Some("子项目"), Some("1-1"), Some("250$"), Some("没交哈"), Some("本来说的今天交的"), Some(2), Some("251$"), Some(System.currentTimeMillis))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[FinancialReceiptItem].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[FinancialReceiptItem].map {
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

object FinancialRecipientController extends Controller with Secured {

  import controllers.V3.StudentController._

  import controllers.V3.FinancialReceiptController.writeFinancialReceipt
  import controllers.V3.FinancialReceiptController.readFinancialReceipt
  import controllers.V3.FinancialReceiptItemController.writeFinancialReceiptItem
  import controllers.V3.FinancialReceiptItemController.readFinancialReceiptItem
  implicit val writeFinancialRecipient = Json.writes[FinancialRecipient]
  implicit val readFinancialRecipient = Json.reads[FinancialRecipient]

  def info(kg: Long, id: Long): ChildInfo = ChildInfo(Some(s"1_${kg}_${id}"), "小明", "嘟嘟", "2013-01-01", 1,
    Some("http://suoqin-test.u.qiniudn.com/1D8530120BB9780D0A28F8283E117F13"), 1234, Some("苹果班"), Some(1427817610000L), Some(kg), Some("某个胡同"),
    Some(1), Some(1417817610000L))

  val ext: StudentExt = StudentExt(Some("显示名"), Some("曾用名"), Some("3721"), Some("510122201301010274"), Some("四川双流"), Some("农村"),
    Some("中国"), Some("湖北武汉"), Some("傣族"), Some(1), Some("2015-09-01"), Some("骑马，打仗"), Some("1-2-3322"), Some("这家伙很懒，什么也没留下。"), Some(1), Some("青霉素过敏"))


  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(FinancialRecipient(Some(1), FinancialReceipt(Some(1), Some("123131312312"), Some("刷卡"), Some(321), Some(s"3_${kg}_1"), Some(System.currentTimeMillis)), Student(Some(1), info(kg, 1), ext)))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(FinancialRecipient(Some(1), FinancialReceipt(Some(1), Some("123131312312"), Some("刷卡"), Some(321), Some(s"3_${kg}_1"), Some(System.currentTimeMillis)), Student(Some(1), info(kg, 1), ext))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[FinancialRecipient].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[FinancialRecipient].map {
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