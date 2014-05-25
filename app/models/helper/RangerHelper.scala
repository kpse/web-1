package models.helper

object RangerHelper {
  def generateFrom(from: Option[Long], field: Option[String]) = {
    from map {
      f =>
        " and %s > {from}".format(field.getOrElse("uid"))
    }
  }

  def generateTo(to: Option[Long], field: Option[String]) = {
    to map {
      t =>
        " and %s < {to}".format(field.getOrElse("uid"))
    }
  }

  def rangerQuery(from: Option[Long], to: Option[Long]) = {
    //if from or to is a timestamp , it should be bigger than 1388534400000
    // range should be timestamp by default
    from.getOrElse(0L) + to.getOrElse(0L) match {
      case range if range > 1388534400000L || range == 0 =>
        rangerQueryWithField(from, to, Some("update_at"))
      case _ =>
        rangerQueryWithField(from, to, None)
    }

  }

  def rangerQueryWithField(from: Option[Long], to: Option[Long], field: Option[String]) = {
    " " + generateFrom(from, field).getOrElse("") + generateTo(to, field).getOrElse("") + " order by " + field.getOrElse("uid") + " DESC"
  }

}
