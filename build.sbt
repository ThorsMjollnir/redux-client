
enablePlugins(ScalaJSPlugin)

name := "intake24-js-client"

version := "1.0.0"

scalaVersion := "2.12.4"

scalacOptions += "-P:scalajs:sjsDefinedByDefault"

scalaJSLinkerConfig ~= {
  _.withModuleKind(ModuleKind.CommonJSModule)
}

val npmPackageSrc = SettingKey[File]("npmPackageSrc", "Source directory for NPM package files")

npmPackageSrc := baseDirectory.value / "src" / "main" / "npm"

val npmPackageTarget = SettingKey[File]("npmPackageTarget", "Target directory for NPM package files")

npmPackageTarget := target.value / "npm"

val packageForNpm = TaskKey[Unit]("packageForNpm", "Copy NPM files")

packageForNpm := {

  val log = streams.value.log
  val jsFile = (fastOptJS in Compile).value.data

  log.info("Creating files for local NPM module...")

  IO.copyDirectory(npmPackageSrc.value, npmPackageTarget.value, true)
  IO.copyFile(jsFile, new java.io.File(npmPackageTarget.value, "index.js"), true)
}

val circeVersion = "0.8.0"

libraryDependencies ++= Seq(
  "io.circe" %%% "circe-core",
  "io.circe" %%% "circe-generic",
  "io.circe" %%% "circe-parser"
).map(_ % circeVersion)