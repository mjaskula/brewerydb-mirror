import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "brewerydb-mirror"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    
    "com.google.inject" % "guice" % "3.0",
    "com.tzavellas" % "sse-guice" % "0.7.1",
    
    "org.mongodb" %% "casbah" % "2.5.0",
    "com.novus" %% "salat-core" % "1.9.2-SNAPSHOT",
    "se.radley" %% "play-plugins-salat" % "1.2",
    "joda-time" % "joda-time" % "2.1",
    
    "org.testng" % "testng" % "6.8" % "test",
    "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    routesImport += "se.radley.plugin.salat.Binders._",
    templatesImport += "org.bson.types.ObjectId"
  )

}
