package models.V8

import org.joda.time.DateTime
import StubNews.allNews

case class KulebaoNews(id: Option[Long], title: String, content: String, author: String, created_at: Option[Long]) {
  def displayTime = new DateTime(created_at.getOrElse(0)).toString("yyyy-MM-dd")
  def displayTimeShort = new DateTime(created_at.getOrElse(0)).toString("MM-dd")
  def revisedStyleContent = content.replaceAll("<p>", "<p><span>&nbsp; &nbsp; &nbsp;&nbsp; </span>")
}

object KulebaoNews {
  def findById(id: Long) = allNews.find( x => x.id.equals(Some(id)) )
  def nextId(news: KulebaoNews): Long = findNext(news.id.getOrElse(0), allNews.reverse.flatMap(_.id))


  def prevId(news: KulebaoNews): Long = findNext(news.id.getOrElse(0), allNews.flatMap(_.id))

  private def findNext(current: Long, all: List[Long]): Long = {
    all.dropWhile(_ != current) match {
      case _::x::xs => x
      case _ => 0
    }
  }

  def top5News: List[KulebaoNews] = allNews.take(5)
  def top10News: List[KulebaoNews] = allNews.take(10)
}
