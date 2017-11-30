package uk.ac.ncl.openlab.intake24.js.redux

import uk.ac.ncl.openlab.intake24.js.HttpClient

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel, JSGlobalScope}
import js.JSConverters._
import scala.scalajs.js.{JSConverters, UndefOr}


@js.native
@JSGlobalScope
object Globals extends js.Object {
  var console: js.Dynamic = js.native
  var window: js.Dynamic = js.native
}

@js.native
trait Logger extends js.Object {
  def debug(message: js.Any)

  def info(message: js.Any)

  def error(message: js.Any)
}

@JSExportTopLevel("App")
class App(httpClient: HttpClient, logger: Logger, namespace: String) {

  @JSExport
  def getReducer(): js.Function =
    (previousState: UndefOr[js.Dynamic], action: js.Dynamic) =>

      if (previousState.isEmpty)
        AppState()
      else
        ActionUtils.fromJs(action) match {
          case Some(action) => reducerImpl(logger, previousState.get.asInstanceOf[AppState], action)
          case None =>
            logger.debug("Ignored unrecognized message:")
            logger.debug(action)
            previousState
        }

  private def reducerImpl(logger: Logger, previousState: AppState, action: Intake24Action): AppState = action match {
    case AddItem(name) => previousState.copy(items = previousState.items :+ name)
    case DeleteItem(index) => previousState.copy(items = previousState.items.take(index) ++ previousState.items.drop(index + 1))
  }

  @JSExport
  def init(store: Store): Unit =
    store.subscribe(() => {
      logger.debug(store.getState())
    })

}