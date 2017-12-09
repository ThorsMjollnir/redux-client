package uk.ac.ncl.openlab.intake24.redux.toolbox

import io.circe.{Decoder, Json, JsonObject}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import io.circe.scalajs.convertJsToJson
import io.circe.scalajs.convertJsonToJs
import io.circe.syntax._
import io.circe.generic.auto._
import uk.ac.ncl.openlab.intake24.redux.JSConsole

sealed trait ToolboxAction

case object Init extends ToolboxAction

case object ReloadLocales extends ToolboxAction

case class SetLocales(locales: Seq[String]) extends ToolboxAction

object ToolboxActions {

  def toJs(action: ToolboxAction): js.Any = {

    val payload = action match {
      case Init => JsonObject.empty
      case ReloadLocales => JsonObject.empty
      case a: SetLocales => a.asJsonObject
    }

    convertJsonToJs(Json.fromJsonObject(payload.add("type", Json.fromString(action.getClass.getName))))
  }

  def parsePayload[T](action: js.Dynamic)(implicit decoder: Decoder[T]): Option[T] =
    (for (json <- convertJsToJson(action);
          parsed <- json.as[T])
      yield parsed) match {
      case Left(err) =>
        JSConsole.error("Failed to parse action!")
        err.printStackTrace()
        None
      case Right(res) =>
        Some(res)
    }

  // Must be a better way to do this...

  def fromJs(action: js.Dynamic): Option[ToolboxAction] = {
    val t = action.`type`.asInstanceOf[String]

    if (t == ReloadLocales.getClass.getName)
      Some(ReloadLocales)
    else if (t == SetLocales.getClass.getName)
      parsePayload[SetLocales](action)
    else if (t == Init.getClass.getName)
      Some(Init)
    else
      None
  }

  @JSExport
  def reloadLocales() = toJs(ReloadLocales)
}