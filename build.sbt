import java.io.IOException
import java.nio.charset.StandardCharsets

enablePlugins(ScalaJSPlugin)

name := "intake24-redux-client"

description := "Intake24 Redux interface for JavaScript platforms"

version := "0.1.0"

scalaVersion := "2.12.4"

scalacOptions += "-P:scalajs:sjsDefinedByDefault"

scalaJSLinkerConfig ~= {
  _.withModuleKind(ModuleKind.CommonJSModule)
}

libraryDependencies += "uk.ac.ncl.openlab.intake24" %%% "api-client" % "1.0.0-SNAPSHOT"

val packageForNpm = TaskKey[Unit]("packageForNpm", "Package final JavaScript as NPM module")

packageForNpm := {

  val log = streams.value.log
  val jsFile = (fastOptJS in Compile).value.data
  val jsFileName = jsFile.getName

  val _name = name.value
  val _version = version.value
  val _description = description.value

  log.info("Building NPM module...")

  val npmTarget = target.value / "npm"

  log.info("Writing package.json...")

  val _package =
    s"""{
       |  "name": "${_name}",
       |  "version": "${_version}",
       |  "description": "${_description}",
       |  "main": "${jsFileName}",
       |  "scripts": {
       |    "test": "echo \\"Error: no test specified\\" && exit 1"
       |  },
       |  "author": "Ivan Poliakov <ivan.poliakov@ncl.ac.uk>",
       |  "license": "Apache-2.0",
       |  "dependencies": {
       |    "redux": "3.7.2"
       |  }
       |}
       |""".stripMargin

  IO.write(npmTarget / "package.json", _package, StandardCharsets.UTF_8)

  log.info("Copying JavaScript...")

  IO.copyFile(jsFile, npmTarget / jsFileName, CopyOptions(true, false, false))

  log.info("Attempting to run 'npm install' (to enable local development)...")

  // To resolve npm using PATH
  val shellPrefix = if (System.getProperty("os.name").toLowerCase.contains("windows"))
    Seq("cmd", "/C")
  else
    Seq()

  val npmCommand = shellPrefix ++ Seq("npm", "install")

  try {
    val errCode = sys.process.Process(npmCommand, npmTarget).!
    if (errCode != 0)
      log.warn(s"'npm install' failed with error code $errCode")
    else
      log.info("'npm install' successful.")
  } catch {
    case e: IOException =>
      log.warn("Unable to run 'npm install':")
      log.warn(s"  ${e.getMessage}")
  }
}