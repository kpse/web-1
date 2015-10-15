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

  def clearAllCache(prefix: String)(implicit name: String): Unit = {
    val partition = Cache.getAs[List[String]](name).partition(_ startsWith prefix)
    logger.debug(s"prefix = $prefix \n partition1 = ${partition._1} \n partition2 = ${partition._2}")
    partition._2 foreach (_ foreach Cache.remove)
    Cache.set(name, partition._1.toList)
    logger.debug(s"$name with prefix $prefix cache cleaned up!")
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
