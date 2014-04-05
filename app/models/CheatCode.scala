package models

import play.cache.Cache

case class CheatCode(code: String)

object CheatCode {
  val cheatCodeKey = "verification"

  def validate(code: String) = code.nonEmpty && code.equals(Cache.get(cheatCodeKey))
}
