name := "common"

version := "0.0.2-SNAPSHOT"

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


// Publishing settings

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra :=
  <url>https://github.com/lum-ai/common</url>
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
    </license>
  </licenses>
  <scm>
    <url>scm:git:github.com/lum-ai/common</url>
    <connection>scm:git:git@github.com:lum-ai/common.git</connection>
  </scm>
  <developers>
    <developer>
      <id>marcovzla</id>
      <name>Marco Antonio Valenzuela Escárcega</name>
      <url>lum.ai</url>
    </developer>
    <developer>
      <id>ghp</id>
      <name>Gus Hahn-Powell</name>
      <url>lum.ai</url>
    </developer>
  </developers>
