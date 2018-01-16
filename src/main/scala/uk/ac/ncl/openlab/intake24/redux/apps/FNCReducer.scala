package uk.ac.ncl.openlab.intake24.redux.apps

import io.circe.{Decoder, Encoder}
import io.circe.scalajs._
import uk.ac.ncl.openlab.intake24.api.data.UserFoodHeader
import uk.ac.ncl.openlab.intake24.redux.{Reducer, ReduxSumTypeDecoder, ReduxSumTypeEncoder}
import uk.ac.ncl.openlab.intake24.redux.foodsearch.{FoodSearchReducer, FoodSearchState}

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import io.circe.generic.auto._
import shapeless._

sealed trait FNCPrompt

case class FoodSearchPrompt(state: FoodSearchState) extends FNCPrompt

case class FNCState(selectedFood: Option[UserFoodHeader] = None, currentPrompt: FNCPrompt)

@JSExportTopLevel("FNCReducer")
object FNCReducer {

  val initialState: FNCState = FNCState(None, FoodSearchPrompt(FoodSearchReducer.initialState))

  implicit val currentPromptEncoder: Encoder[FNCPrompt] = new ReduxSumTypeEncoder[FNCPrompt]()(cachedImplicit)
  implicit val currentPromptDecoder: Decoder[FNCPrompt] = new ReduxSumTypeDecoder[FNCPrompt]()(cachedImplicit)

  implicit val actionDecoder = new ReduxSumTypeDecoder[FNCAction](Reducer.actionTypePrefix)

  def reducerImpl(previousState: FNCState, action: FNCAction): FNCState = action match {
    case Whatever => previousState
  }

  def nextPrompt(state: FNCState): Option[Reducer[_, _]] = {
    if (state.selectedFood.isEmpty)
      Some(FoodSearchReducer)
    else
      None
  }

  @JSExport
  def create(): js.Function =
    (previousState: UndefOr[js.Any], action: js.Any) =>
      if (previousState.isEmpty)
        initialState.asJsAny
      else {
        val scalaState = decodeJs[FNCState](previousState.get).right.get

        /*
        * 1) Try to apply prompt reducer
        * 2) If prompt state changed, check for prompt completion and copy the state
        */

        scalaState.currentPrompt match {
          case FoodSearchPrompt(state) =>
            FoodSearchReducer.applyToJsAction(state, action) match {
              case Some(newPromptState) =>
                if (newPromptState.selectedFood.isDefined) {
                  // next prompt
                }


            }
        }

        val withNewPromptState = scalaState.copy(currentPrompt = newPromptState)

        val newFncState = decodeJs(action)(actionDecoder).toOption match {
          case Some(fncAction) =>
            reducerImpl(withNewPromptState, fncAction)
          case None =>
            withNewPromptState
        }

        newFncState.asJsAny
      }
}
