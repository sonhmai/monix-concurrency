name := "monix-concurrency"

version := "0.1"

ThisBuild / scalaVersion := "2.13.5"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % "test"
libraryDependencies += "io.monix" %% "monix" % "3.3.0"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4"

libraryDependencies ++= Seq(
  "org.apache.logging.log4j" %% "log4j-api-scala" % "12.0",
  "org.apache.logging.log4j" % "log4j-core" % "2.14.1",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.14.1",
  "org.log4s" %% "log4s" % "1.9.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)
