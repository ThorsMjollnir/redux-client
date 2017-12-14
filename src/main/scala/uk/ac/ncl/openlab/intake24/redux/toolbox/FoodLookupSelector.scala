package uk.ac.ncl.openlab.intake24.redux.toolbox

import io.circe.generic.auto._
import io.circe.scalajs.convertJsonToJs
import io.circe.syntax._
import uk.ac.ncl.openlab.intake24.redux.{JSConsole, Selector, Store}

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

class FoodLookupSelector(val store: Store, val namespace: String) extends Selector[FoodLookupState] {

  @JSExport
  def state() = convertJsonToJs(getState.asJson)
}
