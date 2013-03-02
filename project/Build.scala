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
    
    "org.testng" % "testng" % "6.8" % "test",
    "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
