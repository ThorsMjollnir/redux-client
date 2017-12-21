package uk.ac.ncl.openlab.intake24.redux.apps

import uk.ac.ncl.openlab.intake24.api.client.roshttp.user.FoodDataImpl
import uk.ac.ncl.openlab.intake24.api.data.UserFoodHeader
import uk.ac.ncl.openlab.intake24.redux.{ApiUtils, ModuleStore, Store}
import uk.ac.ncl.openlab.intake24.redux.api.Client

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

import js.JSConverters._

case class FNCState(selectedFood: Option[UserFoodHeader] = None)

@JSExportTopLevel("FoodNutrientsCalculator")
class FoodSearch(val reduxStore: Store, val clientStore: Client, val selector: js.Array[String])
  extends ModuleStore[FNCState, FNCAction] with ApiUtils {

  @JSExport
  val foodSearch = new FoodSearch(reduxStore, clientStore, selector.concat(js.Array("currentPrompt", "state")))

}
