package uk.ac.ncl.openlab.intake24.redux.prompts.foodsearch

import uk.ac.ncl.openlab.intake24.api.data.{FoodDataForSurvey, LookupResult}

sealed trait FoodSearchAction

case class FoodSearchSuccessful(result: LookupResult) extends FoodSearchAction

case class FoodSearchFailed(errorMessage: String) extends FoodSearchAction

case class FoodSearchStarted(query: String) extends FoodSearchAction

case class FoodSearchResultSelected(code: String) extends FoodSearchAction

case class FoodSearchDataReceived(data: FoodDataForSurvey) extends FoodSearchAction