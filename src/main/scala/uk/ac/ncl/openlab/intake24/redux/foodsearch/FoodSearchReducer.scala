package uk.ac.ncl.openlab.intake24.redux.foodsearch

import uk.ac.ncl.openlab.intake24.api.data.LookupResult
import uk.ac.ncl.openlab.intake24.redux.{Macros, Reducer}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

case class FoodSearchState(query: String, searchPending: Boolean, result: LookupResult, errors: Seq[String])

@JSExportTopLevel("FoodSearchReducer")
object FoodSearchReducer extends Reducer[FoodSearchState, FoodSearchAction] {

  val initialState: FoodSearchState = FoodSearchState("", false, LookupResult(Seq(), Seq()), Seq())

  def reducerImpl(previousState: FoodSearchState, action: FoodSearchAction): FoodSearchState = action match {
    case FoodSearchStarted(query) =>
      previousState.copy(query = query, searchPending = true)

    case FoodSearchSuccessful(result) =>
      previousState.copy(searchPending = false, result = result)

    case FoodSearchFailed(errorMessage) =>
      previousState.copy(errors = errorMessage +: previousState.errors)
  }

  def actionFromJs(action: js.Dynamic): Option[FoodSearchAction] = Macros.actionFromJs[FoodSearchAction](action)
}
