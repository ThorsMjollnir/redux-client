package uk.ac.ncl.openlab.intake24.redux.foodsearch

import io.circe.generic.auto._
import io.circe.scalajs._
import uk.ac.ncl.openlab.intake24.api.client.services.user.FoodDataService
import uk.ac.ncl.openlab.intake24.redux.{ApiUtils, Macros, ModuleStore, Store}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport


class FoodSearchStore(val reduxStore: Store, val selector: Seq[String], foodDataService: FoodDataService)
  extends ModuleStore[FoodSearchState, FoodSearchAction] with ApiUtils {

  @JSExport
  def search(description: String) = {

    onComplete(foodDataService.lookup("en_GB", description)) {
      case Right(lookupResult) => dispatch(FoodSearchSuccessful(lookupResult))
      case Left(errorMessage) => dispatch(FoodSearchFailed(errorMessage))
    }

    dispatch(FoodSearchStarted(description))
  }

  def actionToJs(action: FoodSearchAction): js.Any = Macros.actionToJs[FoodSearchAction](action)

  def stateFromJs(state: js.Any): FoodSearchState = decodeJs[FoodSearchState](state).right.get
}
