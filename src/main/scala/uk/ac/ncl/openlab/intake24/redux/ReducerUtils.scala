package uk.ac.ncl.openlab.intake24.redux

import scala.scalajs.js
import scala.scalajs.js.UndefOr

object ReducerUtils {

  private def createUnionReducerImpl(reducers: Map[String, js.Function]): js.Function = {
    (previousState: UndefOr[js.Dynamic], action: js.Any) =>
      if (previousState.isEmpty)
        null
      else {
        val state = previousState.get
        if (state == null)
          previousState
        else {
          val typeName = state.selectDynamic("type").toString
          val wrappedState = state.selectDynamic("state")

          reducers.get(typeName) match {
            case Some(function) =>
              val newWrappedState = function.call(null, wrappedState, action)
              if (newWrappedState != wrappedState)
                js.Dynamic.literal(`type` = typeName, state = newWrappedState)
              else
                previousState

            case None =>
              throw new RuntimeException("Unexpected type in union reducer: " + typeName)
          }
        }
      }
  }

  def createUnionReducer(reducers: Seq[Reducer[_, _]]) = {
    val map = reducers.foldLeft(Map[String, js.Function]()) {
      (map, reducer) => map + (reducer.getClass.getSimpleName -> reducer.create())
    }
    createUnionReducerImpl(map)
  }
}
