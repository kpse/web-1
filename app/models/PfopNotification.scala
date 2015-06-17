package models

import anorm.SqlParser._
import anorm._
import play.Logger
import play.api.db.DB
import play.api.Play.current
import anorm.~
import play.api.libs.json.Json

//  {"code":0,"desc":"The fop was completed successfully",
// "id":"z0.558170767823de5a494cae03",
// "inputBucket":"cocobabys",
// "inputKey":"2001/3_2001_1422359672223/exp_medium/1422774181720.mp4",
// "items":[{"cmd":"avthumb/mp4/vcodec/libx264/acodec/libfaac/stripmeta/1|saveas/Y29jb2JhYnlzOjIwMDEvM18yMDAxXzE0MjIzNTk2NzIyMjMvZXhwX21lZGl1bS8xNDIyNzc0MTgxNzIwLm1wNA==",
// "code":0,"desc":"The fop was completed successfully","hash":"FkMKSCNhFlvk2W4TO33w1mjsZowk",
// "key":"2001/3_2001_1422359672223/exp_medium/1422774181720.mp4"}],
// "pipeline":"0.default",
// "reqid":"ukcAALyx_hlBiOgT"}

case class PfopNotificationItem(code: Int, desc: Option[String], cmd: Option[String], key: Option[String], hash: Option[String])

case class PfopNotification(code: Int, desc: Option[String], id: Option[String], inputBucket: Option[String], inputKey: Option[String], items: List[PfopNotificationItem], pipeline: Option[String], reqid: Option[String]) {
  def save(key: String) = DB.withConnection {
    implicit c =>
      SQL("INSERT INTO pfopresult (`code`, process_key, input_bucket, input_key, `desc`, output_key, pipeline, reqid, updated_at) values " +
        "({code}, {process_key}, {input_bucket}, {input_key}, {desc}, {output_key}, {pipeline}, {reqid}, {updated_at})")
        .on('code -> code, 'process_key -> key, 'input_bucket -> inputBucket, 'input_key -> inputKey,
          'desc -> desc, 'output_key -> items.head.key, 'pipeline -> pipeline, 'reqid -> reqid, 'updated_at -> System.currentTimeMillis).executeInsert()
  }
}

object PfopNotification {
  implicit val readPfopNotificationItem = Json.reads[PfopNotificationItem]
  implicit val readPfopNotification = Json.reads[PfopNotification]
}

