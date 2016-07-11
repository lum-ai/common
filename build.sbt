name := "common"

version := "0.1-SNAPSHOT"

organization := "ai.lum"

scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-feature",
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Ywarn-dead-code",
  "-Ywarn-value-discard",
  "-Ywarn-unused-import",
  "-encoding", "utf8"
)

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.0",
  "org.apache.commons" % "commons-lang3" % "3.4"
)
