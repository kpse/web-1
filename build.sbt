import scala._

name := "kulebao"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.h2database" % "h2" % "1.3.168",
  "mysql" % "mysql-connector-java" % "5.1.27",
  "commons-codec" % "commons-codec" % "1.7",
  "com.qiniu" % "qiniu-java-sdk" % "7.0.+",
  "joda-time" % "joda-time" % "2.3",
  "net.tanesha.recaptcha4j" % "recaptcha4j" % "0.0.7"
)


def customLessEntryPoints(base: File): PathFinder = (
  (base / "app" / "assets" / "stylesheets" ** "*.less")
  )


play.Project.playScalaSettings ++ lesscSettings

lessEntryPoints := Nil

lesscEntryPoints in Compile <<= baseDirectory(customLessEntryPoints)

lesscOptions in Compile := Seq("--no-color", "--yui-compress")

coffeescriptOptions := Seq("bare")

scalacOptions in (Compile,doc) := Seq("-groups", "-implicits")

autoAPIMappings := true


