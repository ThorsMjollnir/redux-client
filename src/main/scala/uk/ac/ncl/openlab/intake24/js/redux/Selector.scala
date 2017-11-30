package uk.ac.ncl.openlab.intake24.js.redux

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.circe.scalajs.convertJsonToJs
import uk.ac.ncl.openlab.intake24.js.api.auth.{AuthenticationSelector, AuthenticationState}

import js.JSConverters._


case class AppState(auth: AuthenticationState = AuthenticationState(),
                    items: Array[String] = Array())

trait TopLevelSelector {
  def appState: AppState
}


@JSExportTopLevel("Selector")
class Selector(val store: Store, val namespace: String) extends TopLevelSelector {

  def appState = store.getState().selectDynamic(namespace).asInstanceOf[AppState]

  val auth = new AuthenticationSelector(this)

  @JSExport
  def getAppState() = convertJsonToJs(appState.asJson)

  @JSExport
  def getItems() = appState.items.toJSArray

}