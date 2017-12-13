package uk.ac.ncl.openlab.intake24.redux

import scala.scalajs.js
import scala.scalajs.js.UndefOr

object ReducerUtils {

  def createJsReducer[S, A](initialState: => S, reducerImpl: (S, A) => S): js.Function =
    (previousState: UndefOr[js.Dynamic], action: js.Dynamic) =>

      if (previousState.isEmpty)
        initialState
      else
        Macros.actionFromJs[A](action) match {
          case Some(action) => reducerImpl(previousState.get.asInstanceOf[S], action)
          case None => previousState
        }
}
