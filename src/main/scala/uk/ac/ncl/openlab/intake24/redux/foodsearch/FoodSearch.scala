package uk.ac.ncl.openlab.intake24.redux.foodsearch

import io.circe.generic.auto._

import uk.ac.ncl.openlab.intake24.api.client.roshttp.user.FoodDataImpl
import uk.ac.ncl.openlab.intake24.redux.api.Client
import uk.ac.ncl.openlab.intake24.redux.{ApiUtils, ModuleStore, Store}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}


@JSExportTopLevel("FoodSearch")
class FoodSearch(val reduxStore: Store, val clientStore: Client, val selector: js.Array[String])
  extends ModuleStore[FoodSearchState, FoodSearchAction] with ApiUtils {

  private val foodDataService = new FoodDataImpl(clientStore.authRequestHandler)

  @JSExport
  def search(description: String) = {

    onComplete(foodDataService.lookup("en_GB", description)) {
      case Right(lookupResult) => dispatch(FoodSearchSuccessful(lookupResult))
      case Left(errorMessage) => dispatch(FoodSearchFailed(errorMessage))
    }

    dispatch(FoodSearchStarted(description))
  }
}
