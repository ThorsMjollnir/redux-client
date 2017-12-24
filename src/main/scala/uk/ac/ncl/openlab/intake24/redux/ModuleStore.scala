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

  protected val reduxActionEncoder = new ReduxSumTypeEncoder[A](Reducer.actionTypePrefix)

  protected def dispatch(action: A) = reduxStore.dispatch(action.asJsAny(reduxActionEncoder))

  @JSExport
  def getState() = {

    js.Dynamic.global.console.warn(reduxStore.getState())
    js.Dynamic.global.console.warn(selector)

    selector.foldLeft(reduxStore.getState()) {
      (obj, path) => obj.selectDynamic(path)
    }
  }

  def getScalaState() = decodeJs[S](getState()).right.get

  protected def subscribe(handler: S => Unit) =
    reduxStore.subscribe(() => {
      handler(getScalaState)
    })
}
