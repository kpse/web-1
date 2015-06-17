package models

import play.api.libs.json.Json

case class UpToken(token: String)
case class Bucket(name: String, key: String) {
  def scope = s"$name:$key"
}

object Bucket {
  implicit val readBucket = Json.reads[Bucket]
  implicit val writesUpToken = Json.writes[UpToken]

  def parse(url: String) = Bucket("cocobabys", "2001/3_2001_1422359672223/exp_medium/1422774181720.mp4")
}
