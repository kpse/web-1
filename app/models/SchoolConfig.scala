package models

import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class ConfigItem(name: String, value: String, category: String = SchoolConfig.SUPER_ADMIN_SETTING) {
  def isExist(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from schoolconfig " +
        " where school_id = {kg} and name={name}")
        .on('kg -> kg.toString, 'name -> name).as(get[Long]("count(1)") single) > 0
  }

  def update(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("update schoolconfig set value={value}, update_at={time}, category={category}" +
        " where school_id = {kg} and name={name}")
        .on('kg -> kg.toString, 'name -> name, 'value -> value, 'category -> category, 'time -> System.currentTimeMillis).executeUpdate()
  }

  def create(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("insert into schoolconfig (school_id, name, value, category, update_at) " +
        " values ({kg}, {name}, {value}, {category}, {time})")
        .on('kg -> kg.toString, 'name -> name, 'value -> value, 'category -> category, 'time -> System.currentTimeMillis()).executeInsert()
  }
}

case class SchoolConfig(school_id: Long, config: List[ConfigItem], school_customized: List[ConfigItem])

object SchoolConfig {
  implicit val configItemWriter = Json.writes[ConfigItem]
  implicit val configItemReader = Json.reads[ConfigItem]
  implicit val schoolConfigWriter = Json.writes[SchoolConfig]
  implicit val schoolConfigReader = Json.reads[SchoolConfig]

  val SUPER_ADMIN_SETTING = "global"
  val SCHOOL_INDIVIDUAL_SETTING = "individual"

  def schoolSmsEnabled(schoolId: Long) = schoolSmsAccount(schoolId).exists(_.nonEmpty) && schoolSmsPassword(schoolId).exists (_.nonEmpty)

  def schoolSmsAccount(schoolId: Long): Option[String] = config(schoolId).config.find(_.name.equalsIgnoreCase("smsPushAccount")) map (_.value)

  def schoolSmsPassword(schoolId: Long): Option[String] = config(schoolId).config.find(_.name.equalsIgnoreCase("smsPushPassword")) map (_.value)

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
    val items: List[ConfigItem] = List(
      ConfigItem("enableHealthRecordManagement", "true"),
      ConfigItem("enableFinancialManagement", "true"),
      ConfigItem("enableWarehouseManagement", "true"),
      ConfigItem("enableDietManagement", "true"),
      ConfigItem("displayVideoMemberDetail", "false"),
      ConfigItem("schoolGroupChat", "true"),
      ConfigItem("smsPushAccount", ""),
      ConfigItem("smsPushPassword", "")
    ) filterNot { c => configItems.exists(c.name == _.name) }
    items ::: configItems
  }

  def config(kg: Long) = DB.withConnection {
    implicit c =>
      val configItems: List[ConfigItem] = SQL("select * from schoolconfig " +
        " where school_id = {kg}")
        .on('kg -> kg.toString)
        .as(simpleItem *)
      val partitions: (List[ConfigItem], List[ConfigItem]) = appendDefaultValue(configItems).partition(_.category.equalsIgnoreCase(SchoolConfig.SUPER_ADMIN_SETTING))
      SchoolConfig(kg, partitions._1, partitions._2)
  }

  val simpleItem = {
    get[String]("name") ~
    get[String]("category") ~
      get[String]("value") map {
      case name ~ category ~ value =>
        ConfigItem(name, value, category)
    }
  }
}