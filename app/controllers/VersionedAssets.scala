package controllers

import play.api.mvc.PathBindable
import play.api.Play
import Play.current
import java.io.File
import play.Logger

object VersionedAssets {
  def at(file: VersionedAsset) = Assets.at(file.path, file.file)
}

case class VersionedAsset(file: String, path: String = "/public", versionParam: String = "v")

object VersionedAsset {

  implicit def pathBinder = new PathBindable[VersionedAsset] {
    def bind(key: String, value: String) = {
      Right(VersionedAsset(value))
    }

    def unbind(key: String, value: VersionedAsset) = {
      val resourceName = Option(value.path + "/" + value.file).map(name => if (name.startsWith("/")) name else ("/" + name)).get
      val modified = Play.resource(resourceName).flatMap {
        resource =>
          resource.getProtocol match {
            case file if file == "file" => {
              Logger.info(new File(resource.toURI).lastModified().toString)
              Some(new File(resource.toURI).lastModified())
            }
            case _ => None
          }
      }
      modified.map(value.file + "?" + value.versionParam + "=" + _).getOrElse(value.file)
    }
  }

  implicit def toVersionedAsset(path: String): VersionedAsset = VersionedAsset(path)
}