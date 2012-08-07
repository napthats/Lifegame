import sbt._
import sbt.Keys._

object LifegameBuild extends Build {

  lazy val lifegame = Project(
    id = "lifegame",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "LifeGame",
      organization := "com.napthats",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.9.2",
      resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
      libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0.1",
      libraryDependencies += "org.eclipse.jetty" % "jetty-servlet" % "8.1.5.v20120716",
      libraryDependencies += "org.eclipse.jetty" % "jetty-server" % "8.1.5.v20120716",
      libraryDependencies += "org.eclipse.jetty" % "jetty-websocket" % "8.1.5.v20120716",
      libraryDependencies += "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" artifacts (Artifact("javax.servlet", "jar", "jar"))
    )
  )
}
