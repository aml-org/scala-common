import sbtcrossproject.CrossPlugin.autoImport.crossProject

version in ThisBuild := getVersion(1, 0)

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
  )

lazy val commonJVM = common.jvm.in(file("./jvm"))
lazy val commonJS  = common.js.in(file("./js"))

def getVersion(major: Int, minor: Int): String = {

  lazy val build = sys.env.getOrElse("BUILD_NUMBER", "0")
  lazy val branch = sys.env.get("BRANCH_NAME")

  if (branch.contains("master")) s"$major.$minor.$build" else s"$major.${minor + 1}.0-SNAPSHOT"
}
