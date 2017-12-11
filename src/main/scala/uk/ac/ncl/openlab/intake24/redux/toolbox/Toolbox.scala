package uk.ac.ncl.openlab.intake24.redux.toolbox

import uk.ac.ncl.openlab.intake24.redux.{JSConsole, Store}

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

case class LocalesState(values: Option[Seq[String]] = None,
                        loading: Boolean = false)

case class ToolboxState(locales: LocalesState = LocalesState(),
                        searchQuery: Option[String] = None)

@JSExportTopLevel("Toolbox")
class Toolbox(namespace: String) {

  @JSExport
  val actions = ToolboxActions

  @JSExport
  def getReducer(): js.Function =
    (previousState: UndefOr[js.Dynamic], action: js.Dynamic) =>

      if (previousState.isEmpty)
        ToolboxState()
      else
        ToolboxActions.actionFromJs(action) match {
          case Some(action) => reducerImpl(previousState.get.asInstanceOf[ToolboxState], action)
          case None =>
            JSConsole.log("Ignored unrecognized message:", action)
            previousState
        }

  private def reducerImpl(previousState: ToolboxState, action: ToolboxAction): ToolboxState = action match {
    case Init =>
      previousState

    case ReloadLocales => {
      previousState.copy(locales = LocalesState(None, true))
    }

  }

  def onStateChanged(store: Store, state: ToolboxState): Unit = {
    JSConsole.log("State changed!")

    if (state.locales.values.isEmpty && !state.locales.loading)
      store.dispatch(ToolboxActions.reloadLocales())
  }

  @JSExport
  def init(store: Store): ToolboxSelector = {
    val selector = new ToolboxSelector(store, namespace)
    store.subscribe(() => onStateChanged(store, selector.getState))
    store.dispatch(ToolboxActions.actionToJs(Init))
    selector
  }
}