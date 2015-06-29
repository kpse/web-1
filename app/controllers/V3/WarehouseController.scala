package controllers.V3

import controllers.Secured
import models.V3._
import models.V3.Inventory._
import models.V3.Stocking._
import models.V3.Goods._
import models.{ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object WarehouseController extends Controller with Secured {

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(Warehouse.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Warehouse.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的仓库。(No such warehouse)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Warehouse].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Warehouse].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Warehouse.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
* --仓库定义细节
create table IMSInfo.dbo.WarehouseInfoItem(
SysNO int identity(1,1),系统编号
WHSysNO int,仓库编号
UserSysNO int用户编号
)

--货物来源
create table IMSInfo.dbo.GoodsOrigin(
SysNO int identity(1,1),系统编号
Name nvarchar(50),货物来源名称
ShortName nvarchar(20),助记码
WarehouseSysNO int,仓库编号
Memo nvarchar(500)备注
)

--货物信息
create table IMSInfo.dbo.GoodsInfo(
SysNO int identity(1,1),系统编号
[Name] nvarchar(50),货物名称
ShortName nvarchar(20),货物助记码
Unit nvarchar(10),货物单位
MinWarn nvarchar(10),最小储量报警值
MaxWarn nvarchar(10),最大储量报警值
StockPlace nvarchar(50),存放位置
WarehouseSysNO int,仓库编号
Memo nvarchar(500)备注
)

--货物出入
create table IMSInfo.dbo.Stocking(
SysNO int identity(1,1),系统编号
StockType char(1),存储类型（进，出）
InvoiceType varchar(3),单据类型编号
InvoiceTypeName nvarchar(20),单据类型（采购入库，盘盈入库，其他入库）
SerialNumber varchar(20),流水号
SNBase int,流水号基值
Creator nvarchar(20),创建人
CreateTime datetime,创建时间
EmployeeSysNO int,采购人编号
WarehouseSysNO int,仓库编号
Memo nvarchar(500)备注
)

--货物出入明细
create table IMSInfo.dbo.StockingItem(
SysNO int identity(1,1),系统编号
StockSysNO int,单据编号
GoodsSysNO int,货物编号
GoodsName nvarchar(50),货物名称
OriginSysNO int,来源编号
OriginName nvarchar(50),来源名称
Value nvarchar(20),价格
QTY nvarchar(20),单行数量
Unit nvarchar(10),单位
Memo nvarchar(500),备注
SUMValue nvarchar(20)单行总价
)

--库存信息
create table IMSInfo.dbo.Inventory(
SysNO int identity(1,1),系统编号
GoodsSysNO int,货物编号
GoodsName nvarchar(50),货物名称
QTY nvarchar(20),数量
Unit nvarchar(10),单位
CreateTime datetime,创建时间
LastEditTime datetime,上次库存变动时间
WarehouseSysNO int仓库编号
)
*/

object WarehouseInventoryController extends Controller with Secured {

  def index(kg: Long, warehouseId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(Inventory.index(kg, warehouseId, from, to, most)))
  }

  def show(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn { u => _ =>
    Inventory.show(kg, warehouseId, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的库存信息。(No such warehouse inventory)")))
    }
  }

  def create(kg: Long, warehouseId: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Inventory].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(kg, warehouseId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Inventory].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(kg, warehouseId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn { u => _ =>
    Inventory.deleteById(kg, warehouseId, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}

object GoodsController extends Controller with Secured {

  def index(kg: Long, warehouseId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(Goods.index(kg, warehouseId, from, to, most)))
  }

  def show(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn { u => _ =>
    Goods.show(kg, warehouseId, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的物资信息。(No such warehouse goods)")))
    }
  }

  def create(kg: Long, warehouseId: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Goods].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(kg, warehouseId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Goods].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(kg, warehouseId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn { u => _ =>
    Goods.deleteById(kg, warehouseId, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}

object StockingController extends Controller with Secured {

  def index(kg: Long, warehouseId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(Stocking.index(kg, warehouseId, from, to, most)))
  }

  def show(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn { u => _ =>
    Stocking.show(kg, warehouseId, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的货物出入信息。(No such warehouse goods)")))
    }
  }

  def create(kg: Long, warehouseId: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Stocking].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(kg, warehouseId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Stocking].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(kg, warehouseId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn { u => _ =>
    Stocking.deleteById(kg, warehouseId, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}