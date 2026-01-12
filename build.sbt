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

ThisBuild / crossScalaVersions := Seq(scala212, scala211, scala213, scala3) // scala30, scala31, scala32, scala33, scala34, scala35, scala36, scala37)
ThisBuild / scalaVersion := scala212

scalacOptions ++= {
  val deprecationOpt = CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 11)) => None // Some("-deprecation")
    case Some((2, 12)) => None // Some("-deprecation")
    case Some((2, 13)) => Some("-Wconf:cat=deprecation:ws")
    case Some((3, 0)) => None // Nothing works, so warnings will be displayed.
    case Some((3, _)) => Some("-Wconf:cat=deprecation:silent")
    case _ => ???
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
  val rewriteSeq = CrossVersion.partialVersion(scalaVersion.value) match {
    // The rewrite is only needed once.
    // case Some((3, 5)) => Seq("-rewrite", "-source", "3.4-migration") // good
    case _ => Seq.empty
  }

  Seq(
    "-encoding", "utf-8",
//    "-explainTypes",
    "-feature",
    "-unchecked",
//    "-Xfatal-warnings"
  ) ++ deprecationOpt ++ higherKindsOpt ++ futureOpt ++ lintOpt ++ rewriteSeq
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
enablePlugins(SiteScaladocPlugin)
enablePlugins(GhpagesPlugin)
git.remoteRepo := "git@github.com:lum-ai/common.git"


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
