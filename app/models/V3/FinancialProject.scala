package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.Logger
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class FinancialProject(id: Option[Long], parent_id: Option[Long], group_id: Option[Long], name: Option[String], short_name: Option[String],
                            project_id: Option[String], total: Option[String], memo: Option[String]) {
  def exists(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("select count(1) from financialproject where uid={id}")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def handle(id: Long) = exists(id) match {
    case true =>
      update(id)
    case false =>
      create(id)
  }


  def update(kg: Long): Option[FinancialProject] = DB.withConnection {
    implicit c =>
      SQL("update financialproject set parent_id={parent_id}, name={name}, short_name={short_name}, project_id={project_id}, " +
        "total={total}, memo={memo}, updated_at={time}, {group_id}={group_id} where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'group_id -> group_id,
          'name -> name,
          'short_name -> short_name,
          'parent_id -> parent_id,
          'project_id -> project_id,
          'total -> total,
          'memo -> memo,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      FinancialProject.show(kg, id.getOrElse(0))
  }

  def create(kg: Long): Option[FinancialProject] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into financialproject (school_id, parent_id, name, short_name, project_id, group_id, total, memo, updated_at) values (" +
        "{school_id}, {parent_id}, {name}, {short_name}, {project_id}, {group_id}, {total}, {memo}, {time})")
        .on(
          'school_id -> kg,
          'group_id -> group_id,
          'parent_id -> parent_id,
          'name -> name,
          'short_name -> short_name,
          'project_id -> project_id,
          'total -> total,
          'memo -> memo,
          'time -> System.currentTimeMillis
        ).executeInsert()
      FinancialProject.show(kg, insert.getOrElse(0))
  }
}

case class FinancialProjectGroup(id: Option[Long], name: Option[String], short_name: Option[String], projects: Option[List[FinancialProject]]) {
  def exists(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("select count(1) from financialprojectgroup where uid={id}")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def handle(id: Long) = exists(id) match {
    case true =>
      update(id)
    case false =>
      create(id)
  }


  def update(kg: Long): Option[FinancialProjectGroup] = DB.withTransaction {
    implicit c =>
      try {
        SQL("update financialprojectgroup set name={name}, short_name={short_name}, group_id={group_id}, updated_at={time} where school_id={school_id} and uid={id}")
          .on(
            'id -> id,
            'school_id -> kg,
            'name -> name,
            'short_name -> short_name,
            'time -> System.currentTimeMillis
          ).executeUpdate()
        projects foreach {
          _ foreach {
            _.handle(kg)
          }
        }
        c.commit()
        FinancialProjectGroup.show(kg, id.getOrElse(0))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }

  def create(kg: Long): Option[FinancialProjectGroup] = DB.withTransaction {
    implicit c =>
      try {
        val insert: Option[Long] = SQL("insert into financialprojectgroup (school_id, group_id, name, short_name, updated_at) values (" +
          "{school_id}, {group_id}, {name}, {short_name}, {time})")
          .on(
            'school_id -> kg,
            'name -> name,
            'short_name -> short_name,
            'time -> System.currentTimeMillis
          ).executeInsert()
        projects foreach {
          _ foreach {
            _.handle(kg)
          }
        }
        c.commit()
        FinancialProjectGroup.show(kg, insert.getOrElse(0))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }
}


case class FinancialReceiptItem(id: Option[Long], project_id: Option[String], sum_value: Option[String], reason: Option[String], count: Option[Int], updated_at: Option[Long]) {
  def exists(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("select count(1) from financialreceiptitem where uid={id}")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def handle(id: Long, base: Long) = exists(id) match {
    case true =>
      update(id, base: Long)
    case false =>
      create(id, base: Long)
  }


  def update(kg: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL("update financialreceiptitem set project_id={project_id}, sum_value={sum_value}, reason={reason}, count={count}, " +
        "updated_at={time} where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'project_id -> project_id,
          'sum_value -> sum_value,
          'reason -> reason,
          'count -> count,
          'time -> System.currentTimeMillis
        ).executeUpdate()
  }

  def create(kg: Long, base: Long) = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into financialreceiptitem (school_id, receipt_id, project_id, sum_value, reason, count, updated_at) values (" +
        "{school_id}, {receipt_id}, {project_id}, {sum_value}, {reason}, {count}, {time})")
        .on(
          'school_id -> kg,
          'project_id -> project_id,
          'receipt_id -> base,
          'sum_value -> sum_value,
          'reason -> reason,
          'count -> count,
          'time -> System.currentTimeMillis
        ).executeInsert()
  }
}

case class FinancialReceipt(id: Option[Long], serial_number: Option[String], student_id: Option[Long], payment_type: Option[Int], sn_base: Option[Int],
                            creator: Option[String], updated_at: Option[Long], items: Option[List[FinancialReceiptItem]]) {
  def exists(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("select count(1) from financialreceipt where uid={id}")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def handle(id: Long) = exists(id) match {
    case true =>
      update(id)
    case false =>
      create(id)
  }


  def update(kg: Long): Option[FinancialReceipt] = DB.withTransaction {
    implicit c =>
      try {
        SQL("update financialreceipt set serial_number={serial_number}, student_id={student_id}, payment_type={payment_type}, sn_base={sn_base}, " +
          "creator={creator}, updated_at={time} where school_id={school_id} and uid={id}")
          .on(
            'id -> id,
            'school_id -> kg,
            'serial_number -> serial_number,
            'student_id -> student_id,
            'payment_type -> payment_type,
            'sn_base -> sn_base,
            'creator -> creator,
            'time -> System.currentTimeMillis
          ).executeUpdate()
        id foreach {
          case i =>
            FinancialProject.clean(kg, i)
            items foreach {
              _ foreach {
                _.handle(kg, id.get)
              }
            }
        }
        c.commit()
        FinancialReceipt.show(kg, id.getOrElse(0))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }

  def create(kg: Long): Option[FinancialReceipt] = DB.withConnection {
    implicit c =>
      try {
        val insert: Option[Long] = SQL("insert into financialreceipt (school_id, serial_number, student_id, payment_type, sn_base, creator, updated_at) values (" +
          "{school_id}, {serial_number}, {student_id}, {payment_type}, {sn_base}, {creator}, {time})")
          .on(
            'school_id -> kg,
            'serial_number -> serial_number,
            'student_id -> student_id,
            'payment_type -> payment_type,
            'sn_base -> sn_base,
            'creator -> creator,
            'time -> System.currentTimeMillis
          ).executeInsert()
        insert foreach {
          case i =>
            FinancialProject.clean(kg, i)
            items foreach {
              _ foreach {
                _.handle(kg, i)
              }
            }
        }
        c.commit()
        FinancialReceipt.show(kg, insert.getOrElse(0))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }
}

object FinancialProject {
  implicit val writeFinancialProject = Json.writes[FinancialProject]
  implicit val readFinancialProject = Json.reads[FinancialProject]
  implicit val writeFinancialProjectGroup = Json.writes[FinancialProjectGroup]
  implicit val readFinancialProjectGroup = Json.reads[FinancialProjectGroup]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from financialproject where school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to,
          'most -> most
        ).as(simple *)
  }

  def indexInGroup(kg: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from financialproject where school_id={kg} and status=1 and group_id={base}")
        .on(
          'kg -> kg.toString,
          'base -> base
        ).as(simple *)
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from financialproject where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update financialproject set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
  }

  val simple = {
    get[Long]("uid") ~
      get[Option[Long]]("parent_id") ~
      get[Option[String]]("name") ~
      get[Option[String]]("short_name") ~
      get[Option[String]]("project_id") ~
      get[Option[Long]]("group_id") ~
      get[Option[String]]("total") ~
      get[Option[String]]("memo") map {
      case id ~ parent_id ~ name ~ short_name ~ project_id ~ group_id ~ total ~ memo =>
        FinancialProject(Some(id), parent_id, group_id, name, short_name, project_id, total, memo)
    }
  }

  def clean(kg: Long, base: Long) = DB.withTransaction {
    implicit c =>
      SQL(s"update financialproject set status=0 where school_id={kg} and status=1 and group_id={base}")
        .on(
          'kg -> kg.toString,
          'base -> base
        ).executeUpdate()
  }
}

object FinancialProjectGroup {
  implicit val writeFinancialProjectGroup = Json.writes[FinancialProjectGroup]
  implicit val readFinancialProjectGroup = Json.reads[FinancialProjectGroup]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from financialprojectgroup where school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to,
          'most -> most
        ).as(simple *)
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from financialprojectgroup where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update financialprojectgroup set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
      FinancialProject.clean(kg, id)
  }

  val simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[Option[String]]("name") ~
      get[Option[String]]("short_name") map {
      case id ~ kg ~ name ~ short_name =>
        FinancialProjectGroup(Some(id), name, short_name, Some(FinancialProject.indexInGroup(kg.toLong, id)))
    }
  }
}


object FinancialReceipt {
  implicit val writeFinancialReceiptItem = Json.writes[FinancialReceiptItem]
  implicit val readFinancialReceiptItem = Json.reads[FinancialReceiptItem]
  implicit val writeFinancialReceipt = Json.writes[FinancialReceipt]
  implicit val readFinancialReceipt = Json.reads[FinancialReceipt]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from financialreceipt where school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to,
          'most -> most
        ).as(simple *)
  }

  def itemIndex(kg: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from financialreceiptitem where school_id={kg} and receipt_id={base} and status=1")
        .on(
          'kg -> kg.toString,
          'base -> base
        ).as(itemSimple *)
  }

  def cleanItems(kg: Long, base: Long) = DB.withTransaction {
    implicit c =>
      SQL(s"update financialreceiptitem set status=0 where school_id={kg} and status=1 and receipt_id={base}")
        .on(
          'kg -> kg.toString,
          'base -> base
        ).executeUpdate()
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from financialreceipt where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update financialreceipt set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
      cleanItems(kg, id)
  }

  val simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[Option[String]]("serial_number") ~
      get[Option[Long]]("student_id") ~
      get[Option[Int]]("payment_type") ~
      get[Option[Int]]("sn_base") ~
      get[Option[String]]("creator") ~
      get[Option[Long]]("updated_at") map {
      case id ~ kg ~ serial_number ~ student_id ~ payment_type ~ sn_base ~ creator ~ time =>
        FinancialReceipt(Some(id), serial_number, student_id, payment_type, sn_base, creator, time, Some(itemIndex(kg.toLong, id)))
    }
  }

  val itemSimple = {
    get[Long]("uid") ~
      get[Option[String]]("project_id") ~
      get[Option[String]]("sum_value") ~
      get[Option[String]]("reason") ~
      get[Option[Int]]("count") ~
      get[Option[Long]]("updated_at") map {
      case id ~ project_id ~ sum_value ~ reason ~ count ~ time =>
        FinancialReceiptItem(Some(id), project_id, sum_value, reason, count, time)
    }
  }
}
