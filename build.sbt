// Latest version numbers were updated on 2026-04-24.
val scala211 = "2.11.12" // up to 2.11.12
val scala212 = "2.12.21" // up to 2.12.21
val scala213 = "2.13.18" // up to 2.13.18
val scala31  = "3.1.3"   // up to 3.1.3
// Only the LTS versions are listed next.
val scala33  = "3.3.7"   // up to 3.3.7
val scala3   = scala31

ThisBuild / crossScalaVersions := Seq(scala212, scala211, scala213, scala3)
ThisBuild / scalaVersion := scala212
ThisBuild / versionScheme := Some("early-semver")

name := "common"

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

// scaladoc hosting
enablePlugins(SiteScaladocPlugin)
enablePlugins(GhpagesPlugin)
git.remoteRepo := "git@github.com:lum-ai/common.git"
