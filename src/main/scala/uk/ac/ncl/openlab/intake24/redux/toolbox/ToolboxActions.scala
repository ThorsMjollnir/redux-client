package uk.ac.ncl.openlab.intake24.redux.toolbox


import uk.ac.ncl.openlab.intake24.redux.Macros

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

sealed trait ToolboxAction

case object Init extends ToolboxAction

case object ReloadLocales extends ToolboxAction

case class SetLocales(locales: Seq[String]) extends ToolboxAction

object ToolboxActions {

  def actionFromJs(jsAction: js.Dynamic): Option[ToolboxAction] = Macros.actionFromJs[ToolboxAction](jsAction)

  def actionToJs(scalaAction: ToolboxAction): js.Any = Macros.actionToJs[ToolboxAction](scalaAction)

  @JSExport
  def reloadLocales() = actionToJs(ReloadLocales)
}
