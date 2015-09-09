package controllers.helper

import play.Logger
import play.api.Play.current
import play.api.cache.Cache

import scala.reflect.ClassTag

object CacheHelper {
  def recordCacheKeys(cacheKey: String)(implicit name: String): Unit = {
    Cache.set(name, Cache.getAs[List[String]](name) map (_ :+ cacheKey) getOrElse List())
    Logger.info(s"CacheHelper - $name all keys ${Cache.get(name).mkString(",")}")
  }

  def clearAllCache(implicit name: String): Unit = {
    Cache.getAs[List[String]](name) foreach (_ foreach Cache.remove)
    Cache.set(name, List())
    Logger.info(s"CacheHelper - $name cache clean up!")
  }

  def createKeyCache(implicit name: String): List[String] = {
    Cache.getOrElse[List[String]](name) {
      List()
    }
  }

  def digFromCache[T: ClassTag](cacheKey: String, timeout: Int, callback: () => T)(implicit name: String) = {
    Cache.getOrElse[T](cacheKey, timeout) {
      Logger.info(s"CacheHelper - $name cacheKey: $cacheKey cache missed.")
      val relationships: T = callback()
      recordCacheKeys(cacheKey)(name)
      relationships
    }
  }
}
