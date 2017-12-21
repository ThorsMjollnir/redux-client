package uk.ac.ncl.openlab.intake24.redux.apps

import io.circe.{Json, JsonObject}
import uk.ac.ncl.openlab.intake24.api.data.LookupResult
import uk.ac.ncl.openlab.intake24.redux.{Reducer, ReducerUtils}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel
import io.circe.generic.auto._
import io.circe.scalajs._
import uk.ac.ncl.openlab.intake24.redux.foodsearch.FoodSearchReducer

import scala.scalajs.js.UndefOr

@JSExportTopLevel("FNCReducer")
object FNCReducer extends Reducer[FNCState, FNCAction] {

  val initialState: FNCState = FNCState(None)

  def reducerImpl(previousState: FNCState, action: FNCAction): FNCState = action match {
    case Whatever => previousState
  }

  def nextPrompt(state: FNCState): Option[Reducer[_, _]] = {
    if (state.selectedFood.isEmpty)
      Some(FoodSearchReducer)
    else
      None
  }

  val promptReducer = ReducerUtils.createUnionReducer(Seq(FoodSearchReducer))

  override def create(): js.Function = {
    (previousState: UndefOr[js.Any], action: js.Any) =>
      if (previousState.isEmpty) {

        nextPrompt(initialState) match {
          case Some(initialPrompt) =>

            val promptState =
              JsonObject
                .singleton("type", Json.fromString(initialPrompt.typeName))
                .add("state", initialPrompt.initialStateJson)

            initialStateJson.asObject.get.add("currentPrompt", Json.fromJsonObject(promptState))

          case None => throw new RuntimeException("No prompt avaiable for initial state")
        }


      }

      else
        decodeReduxAction(action) match {
          case Some(scalaAction) =>
            val scalaState = decodeJs[S](previousState.get).right.get
            reducerImpl(scalaState, scalaAction).asJsAny
          case None => previousState
        }
  }

}
