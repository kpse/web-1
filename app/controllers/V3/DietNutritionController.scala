package controllers.V3

import controllers.Secured
import models.V3.{ArrangementGroup, DietArrangement, Menu}
import models.V3.ArrangementGroup._
import models.V3.DietArrangement._
import models.V3.Menu._
import models.{ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

case class Vitamin(va: Option[String], vc: Option[String], vd: Option[String], ve: Option[String], vb1: Option[String],
                   vb2: Option[String], vb3: Option[String], vb5: Option[String], vb6: Option[String], vb9: Option[String], vb12: Option[String], vh: Option[String])
case class Metal(ca: Option[String], k: Option[String], na: Option[String], mg: Option[String], fe: Option[String],
                 zn: Option[String], se: Option[String], cu: Option[String], mn: Option[String], cr: Option[String], mo: Option[String])
case class DietNutrition(id: Option[Long], food_type_id: Option[Long], name: Option[String], alias: Option[String], weight: Option[String], available_weight: Option[String],
                         calorie: Option[String], protein: Option[String], fat: Option[String], carbohydrate: Option[String], ash: Option[String],
                         carotine: Option[String], fibre: Option[String], cholesterol: Option[String], vitamin: Option[Vitamin]
                         , p: Option[String], metal: Option[Metal], store_type: Int = 1, former_id: Option[Long] = None)

case class DietGrade(id: Option[Long], name: Option[String], age_name: Option[String], age_min: Option[String], age_max: Option[String], gender: Option[Int],
                     Labor: Option[String], Joule: Option[String],
                     calorie: Option[String], protein: Option[String], fat: Option[String],
                         carotine: Option[String], choline: Option[String], cholesterol: Option[String], vitamin: Option[Vitamin]
                         , p: Option[String], i: Option[String], f: Option[String], metal: Option[Metal])

object DietNutritionController extends Controller with Secured {

  implicit val writeVitamin = Json.writes[Vitamin]
  implicit val readVitamin = Json.reads[Vitamin]
  implicit val writeMetal = Json.writes[Metal]
  implicit val readMetal = Json.reads[Metal]
  implicit val writeDietNutrition = Json.writes[DietNutrition]
  implicit val readDietNutrition = Json.reads[DietNutrition]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(DietNutrition(Some(6001), Some(1), Some("吃的"), Some("好吃的"), Some("1kg"), Some("20g"), Some("1"), Some("2"), Some("3"), Some("4"), Some("5"), Some("6"), Some("7"), Some("8"),
      Some(Vitamin(Some("21"), Some("221"), Some("21"), Some("12"), Some("12"), Some("31"), Some("14"), Some("12"), Some("12"), Some("12"), Some("12"), Some("12")))
      , Some("9"), Some(Metal(Some("11"), Some("12"), Some("13"), Some("14"), Some("15"), Some("61"), Some("17"), Some("18"), Some("19"), Some("19"), Some("19"))
      ), 1, Some(1)))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(DietNutrition(Some(id + 6000), Some(1), Some("吃的"), Some("好吃的"), Some("1kg"), Some("20g"), Some("1"), Some("2"), Some("3"), Some("4"), Some("5"), Some("6"), Some("7"), Some("8"),
      Some(Vitamin(Some("21"), Some("221"), Some("21"), Some("12"), Some("12"), Some("31"), Some("14"), Some("12"), Some("12"), Some("12"), Some("12"), Some("12")))
      , Some("9"), Some(Metal(Some("11"), Some("12"), Some("13"), Some("14"), Some("15"), Some("61"), Some("17"), Some("18"), Some("19"), Some("19"), Some("19"))
      ), 0, Some(1))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[DietNutrition].map {
      case (s) =>
        Ok(Json.toJson(s.copy(id = Some(6001))))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[DietNutrition].map {
      case (s) =>
        Ok(Json.toJson(s.copy(id = Some(6001))))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
* --膳食营养（本表根据网络数据库生成）
 create table IMSInfo.dbo.DietNutrition(
                            SysNO int identity(1,1),系统编号
                            FoodTypeSysNO int,所属食物编号
                            Name nvarchar(30),营养名称
                            AliasName nvarchar(20),缩写
                            Weight nvarchar(20),标准重量
                            AvailableWeight nvarchar(20),可食用部分重量
                            Calorie nvarchar(20),卡路里
                            Protein nvarchar(20),蛋白质
                            Fat nvarchar(20),脂肪
                            Carbohydrate nvarchar(20),碳水化合物
                            Ash nvarchar(20),灰分
                            Carotine nvarchar(20), 胡萝卜素
                            Fibre nvarchar(20),纤维
                            Cholesterol nvarchar(20),胆固醇
                            VA nvarchar(20),维a
                            VC nvarchar(20),维b
                            VD nvarchar(20), 维d
                            VE nvarchar(20), 维e
                            VB1 nvarchar(20), 维b1
                            VB2 nvarchar(20), 维b2
                            VB3 nvarchar(20), 维b3
                            VB9 nvarchar(20), 维b9
                            Ca nvarchar(20),钙
                            P nvarchar(20),磷
                            K nvarchar(20),钾
                            Na nvarchar(20),钠
                            Mg nvarchar(20),镁
                            Fe nvarchar(20),铁
                            Zn nvarchar(20),锌
                            Se nvarchar(20),硒
                            Cu nvarchar(20),铜
                            Mn nvarchar(20)锰
                            )

--膳食关系
create table IMSInfo.dbo.DietRelationship(
SysNO int identity(1,1),系统编号
MenuSysNO int,菜单编号
NutritionSysNO int,营养编号
Weight nvarchar(20)重量
)

--膳食等级
      create table IMSInfo.dbo.DietGrade(
                            SysNO int identity(1,1),系统编号
                            Name nvarchar(20), 等级名称
                            AgeName nvarchar(20),年龄段名称
                            AgeMin nvarchar(20),最小年龄
                            AgeMax nvarchar(20),最大年龄
                            Gender nvarchar(20),性别
                            Labor nvarchar(20),工作强度
                            Joule nvarchar(20),焦耳
                            Calorie nvarchar(20),卡路里
                            Protein nvarchar(20),下面都一样
                            Fat nvarchar(20),
                            Ca nvarchar(20),
                            P nvarchar(20),
                            K nvarchar(20),
                            Na nvarchar(20),
                            Mg nvarchar(20),
                            Fe nvarchar(20),
                            I nvarchar(20),
                            Zn nvarchar(20),
                            Se nvarchar(20),
                            Cu nvarchar(20),
                            F nvarchar(20),
                            Cr nvarchar(20),
                            Mn nvarchar(20),
                            Mo nvarchar(20),
                            VA nvarchar(20),
                            VB1 nvarchar(20),
                            VB2 nvarchar(20),
                            VB3 nvarchar(20),
                            VB5 nvarchar(20),
                            VB6 nvarchar(20),
                            VB9 nvarchar(20),
                            VB12 nvarchar(20),
                            VC nvarchar(20),
                            VD nvarchar(20),
                            VE nvarchar(20),
                            Choline nvarchar(20),
                            VH nvarchar(20)
                            )


--膳食配餐1
create table IMSInfo.dbo.DietArrangeMaster(
SysNO int identity(1,1),系统编号
MenuSysNO int,菜单编号
MenuName nvarchar(20),菜单名称
ArrangeType char(1),配餐类型，早餐午餐晚餐
ArrangeDate datetime配餐日期
)

--膳食配餐2
create table IMSInfo.dbo.DietArrangeItem(
SysNO int identity(1,1),系统编号
MasterSysNO int,所属配餐编号
GradeSysNO int,等级编号
Weight nvarchar(20)重量
)

--膳食配餐菜单1
create table IMSInfo.dbo.DietBigMenuMaster(
SysNO int identity(1,1),系统编号
BigMenuName nvarchar(20) 存储名称
)

--膳食配餐菜单2
create table IMSInfo.dbo.DietBigMenuItem(
SysNO int identity(1,1),系统编号
BigMenuSysNO int,存储编号
MenuSysNO int,菜单编号
MenuName nvarchar(20),菜单名称
MenuWeight nvarchar(20),菜单重量
ArrangeType varchar(2)配餐类型
)
*/

object DietGradeController extends Controller with Secured {
  implicit val writeVitamin = Json.writes[Vitamin]
  implicit val readVitamin = Json.reads[Vitamin]
  implicit val writeMetal = Json.writes[Metal]
  implicit val readMetal = Json.reads[Metal]
  implicit val writeDietGrade = Json.writes[DietGrade]
  implicit val readDietGrade = Json.reads[DietGrade]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(DietGrade(Some(1), Some("吃的"), Some("不明白"), Some("5"), Some("11"), Some(1), Some("2"), Some("3"), Some("4"), Some("5"), Some("6"), Some("7"), Some("8"),Some("8"),
      Some(Vitamin(Some("21"), Some("221"), Some("21"), Some("12"), Some("12"), Some("31"), Some("14"), Some("12"), Some("12"), Some("12"), Some("12"), Some("12")))
      , Some("9"), Some("10"), Some("19"), Some(Metal(Some("11"), Some("12"), Some("13"), Some("14"), Some("15"), Some("61"), Some("17"), Some("18"), Some("19"), Some("19"), Some("19")))))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(DietGrade(Some(id), Some("吃的"), Some("不明白"), Some("5"), Some("11"), Some(1), Some("2"), Some("3"), Some("4"), Some("5"), Some("6"), Some("7"), Some("8"),Some("8"),
      Some(Vitamin(Some("21"), Some("221"), Some("21"), Some("12"), Some("12"), Some("31"), Some("14"), Some("12"), Some("12"), Some("12"), Some("12"), Some("12")))
      , Some("9"), Some("10"), Some("19"), Some(Metal(Some("11"), Some("12"), Some("13"), Some("14"), Some("15"), Some("61"), Some("17"), Some("18"), Some("19"), Some("19"), Some("19"))))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[DietGrade].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[DietGrade].map {
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

object DietMenuController extends Controller with Secured {

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(Menu.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Menu.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的菜单。(No such diet menu)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Menu].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Menu].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Menu.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}

object DietArrangementGroupController extends Controller with Secured {
  
  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(ArrangementGroup.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    ArrangementGroup.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的膳食分组。(No such diet arrangement group)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[ArrangementGroup].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[ArrangementGroup].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    ArrangementGroup.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}

object DietArrangementController extends Controller with Secured {

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(DietArrangement.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    DietArrangement.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的膳食配餐。(No such diet arrangement)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[DietArrangement].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(kg, 0)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[DietArrangement].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(kg, 0)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    DietArrangement.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}

