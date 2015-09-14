package controllers.helper

import play.api.Logger
import play.api.Play.current
import play.api.cache.Cache

import scala.reflect.ClassTag

case class CacheHelper()
object CacheHelper {
  private val logger: Logger = Logger(classOf[CacheHelper])
  def recordCacheKeys(cacheKey: String)(implicit name: String): Unit = {
    Cache.set(name, Cache.getAs[List[String]](name) map (_ :+ cacheKey) getOrElse List())
    logger.debug(s"$name all keys ${Cache.get(name).mkString(",")}")
  }

  def clearAllCache(implicit name: String): Unit = {
    Cache.getAs[List[String]](name) foreach (_ foreach Cache.remove)
    Cache.set(name, List())
    logger.debug(s"$name cache clean up!")
  }

  def createKeyCache(implicit name: String): List[String] = {
    Cache.getOrElse[List[String]](name) {
      List()
    }
  }

  def digFromCache[T: ClassTag](cacheKey: String, timeout: Int, callback: () => T)(implicit name: String) = {
    Cache.getOrElse[T](cacheKey, timeout) {
      logger.debug(s"$name cacheKey: $cacheKey cache missed.")
      val relationships: T = callback()
      recordCacheKeys(cacheKey)(name)
      relationships
    }
  }
}
