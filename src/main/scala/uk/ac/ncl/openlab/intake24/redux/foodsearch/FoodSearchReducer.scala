package uk.ac.ncl.openlab.intake24.redux.foodsearch

import uk.ac.ncl.openlab.intake24.api.data.{FoodDataForSurvey, LookupResult}
import uk.ac.ncl.openlab.intake24.redux.Reducer

import scala.scalajs.js.annotation.JSExportTopLevel
import io.circe.generic.auto._

case class FoodSearchState(query: String, requestPending: Boolean, result: LookupResult,
                           selectedFoodData: Option[FoodDataForSurvey], errors: Seq[String])

@JSExportTopLevel("FoodSearchReducer")
object FoodSearchReducer extends Reducer[FoodSearchState, FoodSearchAction] {

  val initialState: FoodSearchState = FoodSearchState("", false, LookupResult(Seq(), Seq()), None, Seq())

  def reducerImpl(previousState: FoodSearchState, action: FoodSearchAction): FoodSearchState = action match {
    case FoodSearchStarted(query) =>
      previousState.copy(query = query, requestPending = true)

    case FoodSearchSuccessful(result) =>
      previousState.copy(requestPending = false, result = result)

    case FoodSearchFailed(errorMessage) =>
      previousState.copy(requestPending = false, errors = errorMessage +: previousState.errors)

    case FoodSearchResultSelected(_) =>
      previousState.copy(requestPending = true)

    case FoodSearchDataReceived(data) =>
      previousState.copy(requestPending = false, selectedFoodData = Some(data))
  }
}
