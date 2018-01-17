package uk.ac.ncl.openlab.intake24.redux.apps

import io.circe.{Decoder, Encoder}
import io.circe.scalajs._
import uk.ac.ncl.openlab.intake24.api.data.{FoodDataForSurvey, UserFoodHeader}
import uk.ac.ncl.openlab.intake24.redux.{Reducer, ReduxSumTypeDecoder, ReduxSumTypeEncoder}
import uk.ac.ncl.openlab.intake24.redux.foodsearch.{FoodSearchReducer, FoodSearchState}

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import io.circe.generic.auto._
import shapeless._

sealed trait FNCPrompt {
  def applyReducer(action: js.Any): Option[FNCPrompt]

  def isComplete: Boolean

  def copyResult(fncState: FNCState): FNCState
}

case class FoodSearchPrompt(state: FoodSearchState) extends FNCPrompt {
  def applyReducer(action: js.Any): Option[FNCPrompt] =
    FoodSearchReducer.applyToJsAction(state, action).map(FoodSearchPrompt(_))

  def isComplete = state.selectedFoodData.isDefined

  def copyResult(fncState: FNCState): FNCState = fncState.copy(selectedFoodData = state.selectedFoodData)
}

case class FNCState(selectedFoodData: Option[FoodDataForSurvey] = None, currentPrompt: FNCPrompt)

@JSExportTopLevel("FNCReducer")
object FNCReducer {

  val initialState: FNCState = FNCState(None, FoodSearchPrompt(FoodSearchReducer.initialState))

  implicit val currentPromptEncoder: Encoder[FNCPrompt] = new ReduxSumTypeEncoder[FNCPrompt]()(cachedImplicit)
  implicit val currentPromptDecoder: Decoder[FNCPrompt] = new ReduxSumTypeDecoder[FNCPrompt]()(cachedImplicit)

  implicit val actionDecoder = new ReduxSumTypeDecoder[FNCAction](Reducer.actionTypePrefix)

  def reducerImpl(previousState: FNCState, action: FNCAction): FNCState = action match {
    case Whatever => previousState
  }

  def nextPrompt(state: FNCState): FNCPrompt = {
    if (state.selectedFoodData.isEmpty)
      FoodSearchPrompt(FoodSearchReducer.initialState)
    else
      throw new RuntimeException("No prompt available :(")
  }

  @JSExport
  def create(): js.Function =
    (previousState: UndefOr[js.Any], action: js.Any) =>
      if (previousState.isEmpty)
        initialState.asJsAny
      else {
        var state = decodeJs[FNCState](previousState.get).right.get
        var stateModified = false

        def updateState(newState: FNCState) = {
          state = newState
          stateModified = true
        }

        state.currentPrompt.applyReducer(action).foreach {
          updatedPrompt =>
            if (updatedPrompt.isComplete) {
              updateState(updatedPrompt.copyResult(state))
              updateState(state.copy(currentPrompt = nextPrompt(state)))
            }
            else
              updateState(state.copy(currentPrompt = updatedPrompt))
        }

        decodeJs(action)(actionDecoder).toOption.foreach {
          fncAction =>
            updateState(reducerImpl(state, fncAction))
        }

        if (stateModified)
          state.asJsAny
        else
          previousState
      }
}
