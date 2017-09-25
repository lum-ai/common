import ReleaseTransformations._

name := "common"

organization := "ai.lum"

scalaVersion := "2.11.11"

crossScalaVersions := Seq("2.11.11", "2.12.3")

val commonScalacOptions = Seq(
  "-feature",
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-value-discard",
  "-Ywarn-unused",
  "-encoding", "utf8"
)

scalacOptions ++= commonScalacOptions
scalacOptions += "-Ywarn-unused-import"

// don't use -Ywarn-unused-import in the console
scalacOptions in (Compile, console) := commonScalacOptions

// scope scalacOptions to the doc task to configure scaladoc
scalacOptions in (Compile, doc) += "-no-link-warnings" // suppresses problems with scaladoc @throws links

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.0",
  "org.apache.commons" % "commons-lang3" % "3.4",
  "commons-io" % "commons-io" % "2.5"
)


// release steps
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _)),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
  pushChanges
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
