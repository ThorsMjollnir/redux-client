package uk.ac.ncl.openlab.intake24.redux.apps

import io.circe.Encoder
import io.circe.generic.auto._
import io.circe.scalajs._
import uk.ac.ncl.openlab.intake24.api.data.UserFoodHeader
import uk.ac.ncl.openlab.intake24.redux.{Reducer, ReduxSumTypeDecoder, ReduxSumTypeEncoder}
import uk.ac.ncl.openlab.intake24.redux.foodsearch.{FoodSearchReducer, FoodSearchState}

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}


sealed trait FNCPrompt

case class FoodSearchPrompt(state: FoodSearchState) extends FNCPrompt

case class FNCState(selectedFood: Option[UserFoodHeader] = None, currentPrompt: FNCPrompt)

@JSExportTopLevel("FNCReducer")
object FNCReducer {

  val initialState: FNCState = FNCState(None, FoodSearchPrompt(FoodSearchReducer.initialState))

  implicit val currentPromptEncoder: Encoder[FNCPrompt] = new ReduxSumTypeEncoder[FNCPrompt]()

  val actionDecoder = new ReduxSumTypeDecoder[FNCAction]()

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

        js.Dynamic.global.console.log(action)

        val scalaState = decodeJs[FNCState](previousState.get).right.get

        val newPromptState = scalaState.currentPrompt match {
          case FoodSearchPrompt(state) =>
            decodeJs(action)(FoodSearchReducer.reduxActionDecoder)
              .toOption
              .map(FoodSearchReducer.reducerImpl(state, _))
              .getOrElse(state)
        }

        val withNewPromptState = scalaState.copy(currentPrompt = FoodSearchPrompt(newPromptState))

        val newFncState = decodeJs(action)(actionDecoder).toOption match {
          case Some(fncAction) =>
            reducerImpl(withNewPromptState, fncAction)
          case None =>
            withNewPromptState
        }

        newFncState.asJsAny
      }
}
