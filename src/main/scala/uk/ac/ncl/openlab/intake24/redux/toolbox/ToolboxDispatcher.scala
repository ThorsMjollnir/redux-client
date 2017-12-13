package uk.ac.ncl.openlab.intake24.redux.toolbox


import uk.ac.ncl.openlab.intake24.redux.Dispatcher

import scala.scalajs.js.annotation.JSExport

sealed trait ToolboxAction

case object Init extends ToolboxAction

case object ReloadLocales extends ToolboxAction

case class SetLocales(locales: Seq[String]) extends ToolboxAction

case class Lookup(description: String) extends ToolboxAction

object ToolboxDispatcher extends Dispatcher[ToolboxAction] {

  @JSExport
  def lookup(description: String) = actionToJs(Lookup(description))
}
