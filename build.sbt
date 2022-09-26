import sbtcrossproject.CrossPlugin.autoImport.crossProject

ThisBuild / version := getVersion(1, 0)
ThisBuild / scalacOptions ++= Seq("-feature")
ThisBuild / scalaVersion := "2.12.11"

lazy val common = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
  .settings(
    Common.settings ++ Common.publish ++ Seq(
      organization := "org.mule.common",
      name := "scala-common",
      libraryDependencies ++= Seq(
        "org.scalactic" %%% "scalactic" % "3.0.1" % Test,
        "org.scalatest" %%% "scalatest" % "3.0.0" % Test
      ),
      credentials ++= Common.credentials()
    )
  )
  .jvmSettings(libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided")
  .jsSettings(
    scalaJSModuleKind := ModuleKind.CommonJSModule,
    scalacOptions += "-P:scalajs:suppressExportDeprecations"
  ).disablePlugins(SonarPlugin)


lazy val commonJVM = common.jvm.in(file("./jvm"))
lazy val commonJS  = common.js.in(file("./js"))

def getVersion(major: Int, minor: Int): String = {

  lazy val build  = sys.env.getOrElse("BUILD_NUMBER", "0")
  lazy val branch = sys.env.get("BRANCH_NAME")

  if (branch.contains("master")) s"$major.$minor.$build" else s"$major.${minor + 1}.0-SNAPSHOT"
}

lazy val sonarUrl   = sys.env.getOrElse("SONAR_SERVER_URL", "Not found url.")
lazy val sonarToken = sys.env.getOrElse("SONAR_SERVER_TOKEN", "Not found token.")
lazy val branch     = sys.env.getOrElse("BRANCH_NAME", "develop")

sonarProperties := Map(
  "sonar.login"                      -> sonarToken,
  "sonar.projectKey"                 -> "mulesoft.scala-common",
  "sonar.projectName"                -> "Scala-common",
  "sonar.projectVersion"             -> version.value,
  "sonar.sourceEncoding"             -> "UTF-8",
  "sonar.github.repository"          -> "aml-org/scala-common",
  "sonar.branch.name"                -> branch,
  "sonar.sources"                    -> "shared/src/main/scala",
  "sonar.tests"                      -> "shared/src/test/scala"
)
