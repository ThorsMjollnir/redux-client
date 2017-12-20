package uk.ac.ncl.openlab.intake24.redux

import io.circe.{Decoder, Encoder, Json, JsonObject}
import io.circe.scalajs._
import io.circe.syntax._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

abstract class ModuleStore[S, A](implicit actionEncoder: Encoder[A], stateDecoder: Decoder[S]) {

  val actionTypePrefix = "intake24."

  val reduxStore: Store

  protected val selector: js.Array[String]

  protected def encodeForRedux(action: A): js.Any = {
    val encodedAction = action.asJson.asObject.get

    if (encodedAction.fields.size != 1) {
      throw new IllegalArgumentException("Unexpected encoded action format, expected a " +
        "JSON object with exactly one field for case class name")
    } else {
      val typeName = encodedAction.fields.head
      val payload = encodedAction(typeName).flatMap(_.asObject).get

      val actionInReduxFormat = payload.toVector.foldLeft(JsonObject.singleton("type", Json.fromString(actionTypePrefix + typeName))) {
        case (obj, (k, v)) => obj.add(k, v)
      }

      convertJsonToJs(Json.fromJsonObject(actionInReduxFormat))
    }
  }

  protected def dispatch(action: A) = reduxStore.dispatch(encodeForRedux(action))

  @JSExport
  def getState() = selector.foldLeft(reduxStore.getState()) {
    (obj, path) => obj.selectDynamic(path)
  }

  def getScalaState() = decodeJs[S](getState()).right.get

  protected def subscribe(handler: S => Unit) =
    reduxStore.subscribe(() => {
      handler(getScalaState)
    })
}
