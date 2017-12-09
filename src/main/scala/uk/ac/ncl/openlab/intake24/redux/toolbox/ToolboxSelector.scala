package uk.ac.ncl.openlab.intake24.redux.toolbox

import io.circe.generic.auto._
import io.circe.scalajs.convertJsonToJs
import io.circe.syntax._
import uk.ac.ncl.openlab.intake24.redux.{JSConsole, Store}

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

class ToolboxSelector(val store: Store, val namespace: String)  {

  def getState = {
    JSConsole.log("Trying to get state: ", store.getState(), namespace)
    store.getState().selectDynamic(namespace).asInstanceOf[ToolboxState]
  }

  @JSExport
  def state() = convertJsonToJs(getState.asJson)

  @JSExport
  def locales() = convertJsonToJs(getState.locales.asJson)
}
