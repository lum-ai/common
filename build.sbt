import ReleaseTransformations._

name := "common"

organization := "ai.lum"

val scala211 = "2.11.12" // up to 2.11.12
val scala212 = "2.12.21" // up to 2.12.21
val scala213 = "2.13.17" // up to 2.13.17
val scala30  = "3.0.2"   // up to 3.0.2
val scala31  = "3.1.3"   // up to 3.1.3, for maximum compatibility
val scala32  = "3.2.2"   // up to 3.2.2
val scala33  = "3.3.7"   // up to 3.3.7 (LTS)
val scala34  = "3.4.3"   // up to 3.4.3
val scala35  = "3.5.2"   // up to 3.5.2
val scala36  = "3.6.4"   // up to 3.6.4
val scala37  = "3.7.4"   // up to 3.7.4
val scala3   = scala31

ThisBuild / crossScalaVersions := Seq(scala212, scala211, scala213, scala30, scala31, scala32, scala33, scala34, scala35, scala36, scala37)
ThisBuild / scalaVersion := scala212

scalacOptions ++= {
  val explainTypesOpt = CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 11)) => None
    case Some((2, 12)) => None
    case Some((2, 13)) => None
    case Some((3, 0)) => None
    case Some((3, 1)) => None
    case Some((3, 2)) => None
    case _ => Some("-explainTypes")
  }
  val deprecationOpt = CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, minor)) if minor < 13 => Some("-deprecation")
    // This turns the deprecation warnings into informational messages, which
    // prevents -Xfatal-warnings from failing the compilation for Scala 2.13+.
    // Once cannot easily use @annotation.nowarn("cat=deprecation") in the code
    // because then Scala 2.12 complains about the unnecessary annotation.
    case Some((2, minor)) if minor >= 13 => Some("-Wconf:cat=deprecation:info")

    case Some((3, minor)) if minor < 1 => None
    case Some((3, minor)) if minor >= 1 => Some("-Wconf:cat=deprecation:info")
  }
  val higherKindsOpt = CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, minor)) if minor < 13 => Some("-language:higherKinds")
    case _ => None
  }
  val futureOpt = CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, minor)) if minor < 13 => Some("-Xfuture")
    case _ => None
  }
  val lintOpt = CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, minor)) => Some("-Xlint")
    case _ => None
  }

    Seq(
    "-encoding", "utf-8",
    "-feature",
    "-unchecked",
//    "-Xfatal-warnings"
  ) ++ explainTypesOpt ++ deprecationOpt ++ higherKindsOpt ++ futureOpt ++ lintOpt
}

// the console can't really cope with some scalac flags
Compile / console / scalacOptions --= Seq("-Xlint", "-Xfatal-warnings")

// scope scalacOptions to the doc task to configure scaladoc
Compile / doc / scalacOptions += "-no-link-warnings" // suppresses problems with scaladoc @throws links

libraryDependencies ++= Seq(
  // 3.2.11 of below depends on scala3-library 3.0.2, so use this one for cross-compilation.
  "org.scalatest"          %% "scalatest"               % "3.2.11" % "test",
  // 2.7.0 of below depends on scala3-library 3.0.2, so use this one for cross-compilation.
  "org.scala-lang.modules" %% "scala-collection-compat" % "2.7.0",
  "com.typesafe"            % "config"                  % "1.3.3",
  "org.apache.commons"      % "commons-lang3"           % "3.9",
  "org.apache.commons"      % "commons-text"            % "1.7",
  "commons-io"              % "commons-io"              % "2.6",
  "com.ibm.icu"             % "icu4j"                   % "66.1"
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
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommandAndRemaining("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)


// scaladoc hosting
//enablePlugins(SiteScaladocPlugin)
//enablePlugins(GhpagesPlugin)
//git.remoteRepo := "git@github.com:lum-ai/common.git"


// Publishing settings

publishTo := Some("releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")

publishMavenStyle := true

Test / publishArtifact := false

licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

import xerial.sbt.Sonatype._
sonatypeProjectHosting := Some(GitHubHosting("lum-ai", "common", "marco@lum.ai"))

developers := List(
  Developer(id="marcovzla", name="Marco Antonio Valenzuela Escárcega", email="marco@lum.ai", url=url("https://lum.ai/"))
)
