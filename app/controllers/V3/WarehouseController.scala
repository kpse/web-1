package controllers.V3

import controllers.Secured
import models.{ChildInfo, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

case class Warehouse(id: Option[Long], warehouse_id: Option[Long], employee_id: Option[String])
case class GoodsOrigin(id: Option[Long], name: Option[String], short_name: Option[String], warehouse_id: Option[Long], memo: Option[String])
case class Goods(id: Option[Long], name: Option[String], short_name: Option[String], unit: Option[String], max_warning: Option[String],
                 min_warning: Option[String], warehouse_id: Option[Long], stock_place: Option[String], memo: Option[String], origin: Option[GoodsOrigin])
case class Inventory(id: Option[Long], goods_id: Option[Long], goods_name: Option[String], warehouse_id: Option[Long], created_at: Option[Long], updated_at: Option[Long], quality: Option[Int], unit: Option[String])
case class Stocking(id: Option[Long], `type`: Option[Int], invoice_type: Option[Int], invoice_name: Option[String], serial_number: Option[String], sn_base: Option[String],
                     creator: Option[String], created_at: Option[Long], employee_id: Option[String], warehouse_id: Option[Long], memo: Option[String])
case class StockingDetail(id: Option[Long], stocking_id: Option[Long], goods_id: Option[Long], goods_name: Option[String],
                          origin_id: Option[Long], origin_name: Option[String], price: Option[String], quality: Option[Int], unit: Option[String], memo: Option[String], subtotal: Option[String])

object WarehouseController extends Controller with Secured {

  implicit val writeWarehouse = Json.writes[Warehouse]
  implicit val readWarehouse = Json.reads[Warehouse]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(Warehouse(Some(1), Some(1), Some(s"3_${kg}_12312")))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(Warehouse(Some(1), Some(1), Some(s"3_${kg}_12312"))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Warehouse].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Warehouse].map {
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

  implicit val writeInventory = Json.writes[Inventory]
  implicit val readInventory = Json.reads[Inventory]

  def index(kg: Long, warehouseId: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(Inventory(Some(1), Some(1), Some("铅笔"), Some(warehouseId), Some(System.currentTimeMillis - 500000), Some(System.currentTimeMillis), Some(100), Some("支")))))
  }

  def show(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(Inventory(Some(1), Some(1), Some("铅笔"), Some(warehouseId), Some(System.currentTimeMillis - 500000), Some(System.currentTimeMillis), Some(100), Some("支"))))
  }

  def create(kg: Long, warehouseId: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Inventory].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Inventory].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(new SuccessResponse()))
  }
}

object GoodsController extends Controller with Secured {

  implicit val writeGoodsOrigin = Json.writes[GoodsOrigin]
  implicit val readGoodsOrigin = Json.reads[GoodsOrigin]
  implicit val writeGoods = Json.writes[Goods]
  implicit val readGoods = Json.reads[Goods]

  def index(kg: Long, warehouseId: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(Goods(Some(1), Some("钢笔"), Some("笔"), Some("支"), Some("100"), Some("1000"), Some(warehouseId), Some("地板"), Some("存的是啥？"),
      Some(GoodsOrigin(Some(1), Some("美利坚合众国"), Some("美国"), Some(warehouseId), Some("原产地")))))))
  }

  def show(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(Goods(Some(1), Some("钢笔"), Some("笔"), Some("支"), Some("100"), Some("1000"), Some(warehouseId), Some("地板"), Some("存的是啥？"),
      Some(GoodsOrigin(Some(1), Some("美利坚合众国"), Some("美国"), Some(warehouseId), Some("原产地"))))))
  }

  def create(kg: Long, warehouseId: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Goods].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Goods].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(new SuccessResponse()))
  }
}

object StockingController extends Controller with Secured {

  implicit val writeStocking = Json.writes[Stocking]
  implicit val readStocking = Json.reads[Stocking]

  def index(kg: Long, warehouseId: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(Stocking(Some(1), Some(1), Some(1), Some("发票名字"), Some("12312313123"), Some("123131"), Some("3_2_1"), Some(System.currentTimeMillis), Some("3_2_1"), Some(warehouseId), Some("这啥意思")))))
  }

  def show(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(Stocking(Some(kg), Some(1), Some(1), Some("发票名字"), Some("12312313123"), Some("123131"), Some("3_2_1"), Some(System.currentTimeMillis), Some("3_2_1"), Some(warehouseId), Some("这啥意思"))))
  }

  def create(kg: Long, warehouseId: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Stocking].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Stocking].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, warehouseId: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(new SuccessResponse()))
  }
}

object StockingDetailController extends Controller with Secured {

  implicit val writeStockingDetail = Json.writes[StockingDetail]
  implicit val readStockingDetail = Json.reads[StockingDetail]

  def index(kg: Long, stockingId: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(StockingDetail(Some(1), Some(stockingId), Some(1), Some("一种货"), Some(1), Some("美国"), Some("$1"), Some(122), Some("只"), Some("库存变化"), Some("￥3434")))))
  }

  def show(kg: Long, stockingId: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(StockingDetail(Some(id), Some(stockingId), Some(1), Some("一种货"), Some(1), Some("美国"), Some("$1"), Some(122), Some("只"), Some("库存变化"), Some("￥3434"))))
  }

  def create(kg: Long, stockingId: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[StockingDetail].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, stockingId: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[StockingDetail].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, stockingId: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(new SuccessResponse()))
  }
}
