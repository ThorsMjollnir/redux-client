package uk.ac.ncl.openlab.intake24.redux.apps

import io.circe.{Decoder, Encoder}
import io.circe.scalajs._
import uk.ac.ncl.openlab.intake24.api.data.{FoodDataForSurvey, PortionSizeMethodForSurvey, UserFoodHeader}
import uk.ac.ncl.openlab.intake24.redux.{Reducer, ReduxSumTypeDecoder, ReduxSumTypeEncoder}
import uk.ac.ncl.openlab.intake24.redux.prompts.foodsearch.{FoodSearchReducer, FoodSearchState}

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import io.circe.generic.auto._
import shapeless._
import uk.ac.ncl.openlab.intake24.redux.prompts.portionsize.{MethodSelectorReducer, MethodSelectorState}

sealed trait FNCPrompt {
  def applyReducer(action: js.Any): Option[FNCPrompt]


  def isComplete: Boolean

  def copyResult(fncState: FNCState): FNCState
}

sealed trait FNCPromptRule {
  def apply(appState: FNCState): Option[FNCPrompt]
}

case class FoodSearchPrompt(state: FoodSearchState) extends FNCPrompt {
  def applyReducer(action: js.Any): Option[FNCPrompt] =
    FoodSearchReducer.applyToJsAction(state, action).map(FoodSearchPrompt(_))

  def isComplete = state.selectedFoodData.isDefined

  def copyResult(fncState: FNCState): FNCState = fncState.copy(selectedFoodData = state.selectedFoodData)
}

object FoodSearchPromptRule extends FNCPromptRule {
  def apply(appState: FNCState) =
    if (appState.selectedFoodData.isEmpty)
      Some(FoodSearchPrompt(FoodSearchReducer.initialState))
    else
      None
}

case class SelectPortionSizeMethodPrompt(state: MethodSelectorState) extends FNCPrompt {
  def applyReducer(action: js.Any): Option[FNCPrompt] =
    MethodSelectorReducer.applyToJsAction(state, action).map(SelectPortionSizeMethodPrompt(_))

  def isApplicable(fncState: FNCState) = fncState.selectedPortionSizeMethod.isEmpty

  def isComplete: Boolean = state.selectedIndex.isDefined

  def copyResult(fncState: FNCState): FNCState = fncState.copy(selectedPortionSizeMethod = state.selectedIndex.map(index => state.availableMethods(index)))
}

object SelectPortionSizeMethodPromptRule extends FNCPromptRule {
  def apply(appState: FNCState): Option[FNCPrompt] =
    appState.selectedFoodData.flatMap {
      foodData =>
        if (appState.selectedPortionSizeMethod.isDefined)
          None
        else
          Some(SelectPortionSizeMethodPrompt(MethodSelectorState(foodData.portionSizeMethods.toIndexedSeq, None)))
    }
}

case class FNCState(selectedFoodData: Option[FoodDataForSurvey] = None,
                    selectedPortionSizeMethod: Option[PortionSizeMethodForSurvey] = None,
                    currentPrompt: FNCPrompt)

@JSExportTopLevel("FNCReducer")
object FNCReducer {

  val initialState: FNCState = FNCState(None, None, FoodSearchPrompt(FoodSearchReducer.initialState))

  implicit val currentPromptEncoder: Encoder[FNCPrompt] = new ReduxSumTypeEncoder[FNCPrompt]()(cachedImplicit)
  implicit val currentPromptDecoder: Decoder[FNCPrompt] = new ReduxSumTypeDecoder[FNCPrompt]()(cachedImplicit)

  implicit val actionDecoder = new ReduxSumTypeDecoder[FNCAction](Reducer.actionTypePrefix)


  val promptRules = Seq(FoodSearchPromptRule, SelectPortionSizeMethodPromptRule)


  def reducerImpl(previousState: FNCState, action: FNCAction): FNCState = action match {
    case Whatever => previousState
  }

  def nextPrompt(state: FNCState): FNCPrompt = {
    promptRules.map(_.apply(state)).flatten.headOption match {
      case Some(prompt) => prompt
      case None => throw new RuntimeException("No prompt available :(")
    }
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
