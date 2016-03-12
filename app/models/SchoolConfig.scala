package models

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class ConfigItem(name: String, value: String) {
  def isExist(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from schoolconfig " +
        " where school_id = {kg} and name={name}")
        .on('kg -> kg.toString, 'name -> name).as(get[Long]("count(1)") single) > 0
  }

  def update(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("update schoolconfig set value={value}, update_at={time}" +
        " where school_id = {kg} and name={name}")
        .on('kg -> kg.toString, 'name -> name, 'value -> value, 'time -> System.currentTimeMillis).executeUpdate()
  }

  def create(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("insert into schoolconfig (school_id, name, value, update_at) " +
        " values ({kg}, {name}, {value}, {time})")
        .on('kg -> kg.toString, 'name -> name, 'value -> value, 'time -> System.currentTimeMillis()).executeInsert()
  }
}

case class SchoolConfig(school_id: Long, config: List[ConfigItem])

object SchoolConfig {
  implicit val configItemWriter = Json.writes[ConfigItem]
  implicit val configItemReader = Json.reads[ConfigItem]
  implicit val schoolConfigWriter = Json.writes[SchoolConfig]
  implicit val schoolConfigReader = Json.reads[SchoolConfig]

  def addConfig(kg: Long, config: ConfigItem) = DB.withConnection {
    implicit c =>
      config.isExist(kg) match {
        case true =>
          config.update(kg)
        case false =>
          config.create(kg)
      }

  }

  def appendDefaultValue(configItems: List[ConfigItem]): List[ConfigItem] = {
    val items: List[ConfigItem] = List(ConfigItem("enableHealthRecordManagement", "true"),
      ConfigItem("enableFinancialManagement", "true"),
      ConfigItem("enableWarehouseManagement", "true"),
      ConfigItem("enableDietManagement", "true"),
      ConfigItem("schoolGroupChat", "true")) filterNot { c => configItems.exists(c.name == _.name) }
    items ::: configItems
  }

  def config(kg: Long) = DB.withConnection {
    implicit c =>
      val configItems: List[ConfigItem] = SQL("select name, value from schoolconfig " +
        " where school_id = {kg}")
        .on('kg -> kg.toString)
        .as(simpleItem *)
      SchoolConfig(kg, appendDefaultValue(configItems))
  }

  val simpleItem = {
    get[String]("name") ~
      get[String]("value") map {
      case name ~ value =>
        ConfigItem(name, value)
    }
  }
}