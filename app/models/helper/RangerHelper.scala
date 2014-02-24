package models.helper

object RangerHelper {
  def generateFrom(from: Option[Long]) = {
    from map {
      f =>
        " and uid > {from}"
    }
  }

  def generateTo(to: Option[Long]) = {
    to map {
      t =>
        " and uid < {to}"
    }
  }

  def generateSort(from: Option[Long], to: Option[Long]) = {
    (from, to) match {
      case (Some(f), None) =>
        "asc"
      case _ =>
        "desc"
    }
  }

  def rangerQuery(from: Option[Long], to: Option[Long]) = {
    " " + generateFrom(from).getOrElse("") + generateTo(to).getOrElse("") + " order by uid " + generateSort(from, to)
  }

}
