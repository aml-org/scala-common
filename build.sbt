import sbtcrossproject.CrossPlugin.autoImport.crossProject

lazy val common = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
  .settings(
      Common.settings ++ Common.publish ++ Seq(
          organization := "org.mule.common",
          name := "scala-common",
          version := "0.3.5",
          libraryDependencies ++= Seq(
              "org.scalactic" %%% "scalactic" % "3.0.1" % Test,
              "org.scalatest" %%% "scalatest" % "3.0.0" % Test
          ),
          credentials ++= Common.credentials()
      )
  )
  .jvmSettings(libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided")
  .jsSettings(scalaJSModuleKind := ModuleKind.CommonJSModule)
