package models.helper

object JsonStringHelper {
  def nonEmptyString(input : Option[String]) = Some(input.getOrElse(""))
  implicit def any2JsonString(origin: Option[String]) = new AnyRef {
    def s = nonEmptyString(origin)
  }
}
