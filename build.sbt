import ReleaseTransformations._

name := "common"

organization := "ai.lum"

val scala11 = "2.11.12" // up to 2.11.12
val scala12 = "2.12.13" // up to 2.12.13
val scala13 = "2.13.5"  // up to 2.13.5

crossScalaVersions := Seq(scala12, scala11, scala13)
scalaVersion := crossScalaVersions.value.head

scalacOptions ++= {
  val generalOptions = Seq(
    "-encoding", "utf-8",
    "-explaintypes",
    "-feature",
    "-unchecked"
  )
  val specificOptions = {
    val (major, minor) = CrossVersion.partialVersion(scalaVersion.value).get
    if (major < 2 || (major == 2 && minor <= 12))
      Seq(
        "-deprecation",     // The large number of these in scala13 shall remain hidden.
        "-Xfatal-warnings", // There are warnings for unused imports in scala13.
        "-Xfuture",         // No longer available for scala13.
        "-Xlint",
      )
    else Seq.empty
  }

  generalOptions ++ specificOptions
}

// the console can't really cope with some scalac flags
scalacOptions in (Compile, console) --= Seq("-Xlint", "-Xfatal-warnings")

// scope scalacOptions to the doc task to configure scaladoc
scalacOptions in (Compile, doc) += "-no-link-warnings" // suppresses problems with scaladoc @throws links

libraryDependencies ++= Seq(
  "org.scalatest"     %% "scalatest"     % "3.2.2" % "test",
  "com.typesafe"       % "config"        % "1.3.3",
  "org.apache.commons" % "commons-lang3" % "3.9",
  "org.apache.commons" % "commons-text"  % "1.7",
  "commons-io"         % "commons-io"    % "2.6",
  "com.ibm.icu"        % "icu4j"         % "66.1",
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

publishTo := sonatypePublishToBundle.value

publishMavenStyle := true

publishArtifact in Test := false

licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

import xerial.sbt.Sonatype._
sonatypeProjectHosting := Some(GitHubHosting("lum-ai", "common", "marco@lum.ai"))

developers := List(
  Developer(id="marcovzla", name="Marco Antonio Valenzuela Escárcega", email="marco@lum.ai", url=url("https://lum.ai/"))
)
