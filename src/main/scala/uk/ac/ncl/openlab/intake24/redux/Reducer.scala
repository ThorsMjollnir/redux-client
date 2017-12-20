package uk.ac.ncl.openlab.intake24.redux

import io.circe._
import io.circe.scalajs._

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSExport

abstract class Reducer[S, A](implicit stateDecoder: Decoder[S], stateEncoder: Encoder[S], actionDecoder: Decoder[A]) {

  val actionTypePrefix = "intake24."

  def initialState: S

  def reducerImpl(previousState: S, action: A): S

  def decodeReduxAction(action: js.Any): Option[A] = {
    val json = convertJsToJson(action).toOption.flatMap(_.asObject).getOrElse {
      throw new RuntimeException("Action must be a JSON object")
    }

    val typeName = json("type").flatMap(_.asString).getOrElse {
      throw new RuntimeException("Action must have a string 'type' field")
    }

    if (typeName.startsWith(actionTypePrefix)) {
      val payload = json.filterKeys(_ != "type")
      val actionInCirceFormat = Json.fromJsonObject(JsonObject.singleton(typeName.substring(actionTypePrefix.length), Json.fromJsonObject(payload)))
      actionInCirceFormat.as[A].toOption
    } else
      None
  }

  @JSExport
  def create(): js.Function =
    (previousState: UndefOr[js.Any], action: js.Any) =>
      if (previousState.isEmpty)
        initialState.asJsAny
      else
        decodeReduxAction(action) match {
          case Some(scalaAction) =>
            val scalaState = decodeJs[S](previousState.get).right.get
            reducerImpl(scalaState, scalaAction).asJsAny
          case None => previousState
        }
}
