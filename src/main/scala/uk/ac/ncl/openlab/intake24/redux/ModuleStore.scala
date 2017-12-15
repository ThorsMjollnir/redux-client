package uk.ac.ncl.openlab.intake24.redux

import io.circe.Decoder
import io.circe.scalajs._

import scala.scalajs.js

trait ModuleStore[S, A] {

  val reduxStore: Store

  val selector: Seq[String]

  def dispatch(action: A) = reduxStore.dispatch(actionToJs(action))

  def actionToJs(action: A): js.Any

  def stateFromJs(state: js.Any): S

  def getState() = stateFromJs(selector.foldLeft(reduxStore.getState()) {
    (obj, path) => obj.selectDynamic(path)
  })


  def subscribe(handler: S => Unit) =
    reduxStore.subscribe(() => {
      handler(getState)
    })
}
