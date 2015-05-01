package controllers.V3

import controllers.Secured
import models.{Employee, Parent, ChildInfo, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller
import models.Children.readChildInfo
import models.Children.writeChildInfo

case class EmployeeExt(display_name: Option[String], social_id: Option[String], nationality: Option[String], Residence_place: Option[String],
                       ethnos: Option[String], marriage: Option[Int], education: Option[String], fix_line: Option[String], memo: Option[String],
                        work_id: Option[String], InDate: Option[String], work_status: Option[String], work_title: Option[String], work_rank: Option[String], certification: Option[String])

case class EmployeeV3(id: Option[Long], basic: Employee, ext: EmployeeExt)

object EmployeeControllerV3 extends Controller with Secured {

  implicit val writeEmployeeExt = Json.writes[EmployeeExt]
  implicit val readEmployeeExt = Json.reads[EmployeeExt]

  implicit val writeEmployeeV3 = Json.writes[EmployeeV3]
  implicit val readEmployeeV3 = Json.reads[EmployeeV3]

  def info(kg: Long, id: Long): Employee = Employee(Some(s"3_${kg}_${id}"), "张曼玉", "13991855486", 1, "工作组", "校长",
    Some("http://suoqin-test.u.qiniudn.com/FhdoadN7g_dk3CZBaKi2Q-yG6hEI"), "1979-01-01", kg, "loginAdmin", Some(1427817610000L), Some("principal"),
    Some(1), Some(1417817610000L))

  val ext: EmployeeExt = EmployeeExt(Some("老师显示名"), Some("510122197801010274"), Some("中国"), Some("山西大同"),
    Some("布依族"), Some(0), Some("大学"), Some("028-99997777"), Some("这家伙很懒，什么也没留下。"), Some("44343312"), Some("2013-09-01"), Some("在职"), Some("高级职称"), Some("高级教师"), Some("幼师资格证"))

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(EmployeeV3(Some(1), info(kg, 1), ext))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(EmployeeV3(Some(id), info(kg, id), ext)))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[EmployeeV3].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[EmployeeV3].map {
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
* create table IMSInfo.dbo.EmployeeInfo(
SysNO int identity(1,1), 系统自增编号
Name nvarchar(20), 姓名
DisplayName nvarchar(20), 显示名
Gender int default(0), 性别
SocialID nvarchar(20), 身份证号
Birthday datetime,生日
Nationality nvarchar(20), 国籍
OriginPlace nvarchar(20), 籍贯
Ethnic nvarchar(20), 民族
Marriage nvarchar(20), 婚否（未婚，已婚），可考虑int的枚举
Education nvarchar(20),学历（初中，中专，高中，大专，本科，硕士）
MobilePhone varchar(20),手机
TelePhone varchar(20),座机
Address nvarchar(200),地址
Picture varchar(max),照片
Memo nvarchar(500),备注

WorkID nvarchar(20),工号
WorkGroupSysNO int,所属工作组的编号（教师，职工，医务室）
InDate datetime,入园日期
WorkStatus nvarchar(20),在职状态 enum（在职，离职）
WorkDuty nvarchar(20), 职务（老师，园长等）
WorkTitle nvarchar(20),职称 enum（初级职称，中级职称，高级职称）
WorkRank nvarchar(20),等级，enum（三级教师，二级教师，一级教师，高级教师，特技教师）
Certification nvarchar(20) 两证情况enum（幼师专业证，幼师资格证，两证齐全）
)


*/