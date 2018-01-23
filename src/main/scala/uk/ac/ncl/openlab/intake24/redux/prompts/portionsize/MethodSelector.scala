package uk.ac.ncl.openlab.intake24.redux.prompts.portionsize

import io.circe.generic.auto._
import uk.ac.ncl.openlab.intake24.api.client.roshttp.user.FoodDataImpl
import uk.ac.ncl.openlab.intake24.redux.api.Client
import uk.ac.ncl.openlab.intake24.redux.portionsize.{MethodSelected, MethodSelectorAction}
import uk.ac.ncl.openlab.intake24.redux.{ApiUtils, ModuleStore, Store}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}


@JSExportTopLevel("MethodSelector")
class MethodSelector(val reduxStore: Store, val clientStore: Client, val selector: js.Array[String])
  extends ModuleStore[MethodSelectorState, MethodSelectorAction] with ApiUtils {

  private val foodDataService = new FoodDataImpl(clientStore.authRequestHandler)

  @JSExport
  def selectMethod(index: Int) = {
    dispatch(MethodSelected(index))
  }
}
