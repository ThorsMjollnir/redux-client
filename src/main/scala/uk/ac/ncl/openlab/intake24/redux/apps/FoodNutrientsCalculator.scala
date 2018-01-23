package uk.ac.ncl.openlab.intake24.redux.apps

import uk.ac.ncl.openlab.intake24.redux.api.Client
import uk.ac.ncl.openlab.intake24.redux.prompts.foodsearch.FoodSearch
import uk.ac.ncl.openlab.intake24.redux.{ApiUtils, ModuleStore, Store}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import io.circe.generic.auto._
import uk.ac.ncl.openlab.intake24.redux.prompts.portionsize.MethodSelector

@JSExportTopLevel("FoodNutrientsCalculator")
class FoodNutrientsCalculator(val reduxStore: Store, val clientStore: Client, val selector: js.Array[String])
  extends ModuleStore[FNCState, FNCAction] with ApiUtils {

  @JSExport
  val foodSearch = new FoodSearch(reduxStore, clientStore, selector.concat(js.Array("currentPrompt", "state")))

  @JSExport
  val methodSelector = new MethodSelector(reduxStore, clientStore, selector.concat(js.Array("currentPrompt", "state")))
}
