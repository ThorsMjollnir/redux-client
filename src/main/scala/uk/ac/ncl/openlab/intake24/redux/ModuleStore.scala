package uk.ac.ncl.openlab.intake24.redux

import io.circe.Decoder

import io.circe.scalajs._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

abstract class ModuleStore[S, A](implicit actionEncoder: ActionEncoder[A], stateDecoder: Decoder[S]) {

  val reduxStore: Store

  protected val selector: js.Array[String]

  protected def dispatch(action: A) = reduxStore.dispatch(actionEncoder.encode(action))

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
