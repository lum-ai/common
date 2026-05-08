
ThisBuild / developers := List(
  Developer(id="marcovzla", name="Marco Antonio Valenzuela Escárcega", email="marco@lum.ai", url=url("https://lum.ai/"))
)
ThisBuild / homepage := Some(url(s"https://github.com/lum-ai/common"))
ThisBuild / licenses := Seq(
  "APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")
)
ThisBuild / organization := "ai.lum"
ThisBuild / publishMavenStyle := true
ThisBuild / publishTo := Some("releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
ThisBuild / publishTo := {
  if (isSnapshot.value)
    Some("central-snapshots" at "https://central.sonatype.com/repository/maven-snapshots/")
  else
    localStaging.value
}

Test / packageBin / publishArtifact := false
