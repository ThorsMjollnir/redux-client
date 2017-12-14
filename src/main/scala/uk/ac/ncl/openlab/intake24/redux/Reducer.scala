package uk.ac.ncl.openlab.intake24.redux

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSExport

trait Reducer[S, A] {

  def initialState: S

  def reducerImpl(previousState: S, action: A): S

  @JSExport
  def createReducer(): js.Function =
    (previousState: UndefOr[js.Dynamic], action: js.Dynamic) =>
      if (previousState.isEmpty)
        initialState
      else
        Macros.actionFromJs[A](action) match {
          case Some(action) => reducerImpl(previousState.get.asInstanceOf[S], action)
          case None => previousState
        }
}
