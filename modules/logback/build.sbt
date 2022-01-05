name := "monix-mdc-logback"

version := "0.1"

ThisBuild / scalaVersion := "2.13.5"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % "test"
libraryDependencies += "io.monix" %% "monix" % "3.3.0"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4"

libraryDependencies ++= Seq(
  "org.log4s" %% "log4s" % "1.9.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)
