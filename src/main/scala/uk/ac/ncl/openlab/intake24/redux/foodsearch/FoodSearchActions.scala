package uk.ac.ncl.openlab.intake24.redux.foodsearch

import uk.ac.ncl.openlab.intake24.api.data.LookupResult

sealed trait FoodSearchAction

case class FoodSearchSuccessful(result: LookupResult) extends FoodSearchAction

case class FoodSearchFailed(errorMessage: String) extends FoodSearchAction

case class FoodSearchStarted(query: String) extends FoodSearchAction