package uk.ac.ncl.openlab.intake24.redux.toolbox


import uk.ac.ncl.openlab.intake24.redux.auth.AuthenticationState
import uk.ac.ncl.openlab.intake24.redux.{Macros, ModuleStore, Store}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

import io.circe.scalajs._
import io.circe.generic.auto._

sealed trait ToolboxAction

case object Init extends ToolboxAction

case object ReloadLocales extends ToolboxAction

case class SetLocales(locales: Seq[String]) extends ToolboxAction

case class Lookup(description: String) extends ToolboxAction

case class ToolboxState(auth: AuthenticationState)

class ToolboxStore(val reduxStore: Store, val selector: Seq[String]) extends ModuleStore[ToolboxState, ToolboxAction] {

  @JSExport
  def lookup(description: String) = actionToJs(Lookup(description))

  def actionToJs(action: ToolboxAction): js.Any = Macros.actionToJs[ToolboxAction](action)

  def stateFromJs(state: js.Any): ToolboxState = decodeJs[ToolboxState](state).right.get
}
