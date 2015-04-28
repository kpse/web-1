package models

import play.api.db.DB
import anorm._
import play.api.Play.current
import anorm.SqlParser._

case class ReaderRecord(school_id: Long, reader: String, topic: String, session_id: Long, timestamp: Option[Long] = Some(0))

object ReaderMarker {
  def employeeRead(kg: Long, employeeId: String) = List[ReaderRecord]()


  def recordExists(record: ReaderRecord) = DB.withConnection {
    implicit c =>
      SQL("SELECT count(1) FROM sessionread " +
        "WHERE school_id={kg} AND reader_id={reader} AND topic={topic}")
        .on(
          'kg -> record.school_id.toString,
          'reader -> record.reader,
          'topic -> record.topic
        ).as(get[Long]("count(1)") single) > 0
  }

  def save(record: ReaderRecord) = DB.withConnection {
    implicit c =>
      recordExists(record) match {
        case true =>
          SQL("UPDATE sessionread SET last_read_session_id={session_id}, read_at={read_at} " +
            " WHERE school_id={kg} AND reader_id={reader} AND topic={topic}")
            .on(
              'kg -> record.school_id.toString,
              'reader -> record.reader,
              'topic -> record.topic,
              'session_id -> record.session_id,
              'read_at -> System.currentTimeMillis()
            ).executeUpdate()
        case false =>
          SQL("INSERT INTO sessionread (school_id, reader_id, topic, last_read_session_id, read_at) VALUES " +
            "({kg}, {reader}, {topic}, {session_id}, {read_at})")
            .on(
              'kg -> record.school_id.toString,
              'reader -> record.reader,
              'topic -> record.topic,
              'session_id -> record.session_id,
              'read_at -> System.currentTimeMillis()
            ).executeInsert()
      }
      last(record.school_id, record.topic, record.reader)


  }

  val simple = {
    get[String]("school_id") ~
      get[String]("reader_id") ~
      get[String]("topic") ~
      get[Long]("read_at") ~
      get[Long]("last_read_session_id") map {
      case kg ~ reader ~ topic ~ read_at ~ sessionId =>
        ReaderRecord(kg.toLong, reader, topic, sessionId, Some(read_at))
    }
  }

  def last(kg: Long, topic: String, readerId: String) = DB.withConnection {
    implicit c =>
      SQL("SELECT * FROM sessionread WHERE school_id={kg} AND reader_id={reader} AND topic={topic} LIMIT 1")
        .on(
          'kg -> kg.toString,
          'reader -> readerId,
          'topic -> topic
        ).as(simple singleOpt)
  }

}
