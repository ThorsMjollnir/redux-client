package uk.ac.ncl.openlab.intake24.redux.toolbox

import uk.ac.ncl.openlab.intake24.api.client.services.user.FoodDataService
import uk.ac.ncl.openlab.intake24.api.data.LookupResult
import uk.ac.ncl.openlab.intake24.redux.{JSConsole, Reducer, Store}

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.util.Success

case class LocalesState(values: Option[Seq[String]] = None,
                        loading: Boolean = false)

case class FoodLookupState(searchQuery: String,
                           results: LookupResult)

class FoodLookup(val foodDataService: FoodDataService) extends Reducer[FoodLookupState, ToolboxAction] {

  val initialState = FoodLookupState("", LookupResult(Seq(), Seq()))

  def reducerImpl(previousState: FoodLookupState, action: ToolboxAction): FoodLookupState = action match {
    case Init =>
      previousState

  }

  def onStateChanged(store: Store, state: FoodLookupState): Unit = {
    if (state.searchQuery.nonEmpty)
      foodDataService.lookup("en_GB", state.searchQuery).map {
        case Right(lookupResult) =>

      }


    JSConsole.log("State changed!")

    //  if (state.locales.values.isEmpty && !state.locales.loading)
    //  store.dispatch(ToolboxDispatcher.reloadLocales())
  }

  @JSExport
  def init(store: Store, namespace: String): FoodLookupSelector = {
    val selector = new FoodLookupSelector(store, namespace)
    store.subscribe(() => onStateChanged(store, selector.getState))
    store.dispatch(ToolboxDispatcher.actionToJs(Init))
    selector
  }


}