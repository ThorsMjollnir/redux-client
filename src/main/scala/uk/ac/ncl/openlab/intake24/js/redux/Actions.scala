package uk.ac.ncl.openlab.intake24.js.redux

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

sealed trait Intake24Action

case class AddItem(name: String) extends Intake24Action

case class DeleteItem(index: Int) extends Intake24Action


object ActionUtils {

  val ADD_ITEM = "ADD_ITEM"
  val DELETE_ITEM = "DELETE_ITEM"

  def toJs(action: Intake24Action): js.Dynamic = action match {
    case AddItem(name) => js.Dynamic.literal(`type` = ADD_ITEM, name = name)
    case DeleteItem(index) => js.Dynamic.literal(`type` = DELETE_ITEM, index = index)
  }

  def fromJs(action: js.Dynamic): Option[Intake24Action] =
    action.`type`.asInstanceOf[String] match {
      case ADD_ITEM => Some(AddItem(action.name.asInstanceOf[String]))
      case DELETE_ITEM => Some(DeleteItem(action.index.asInstanceOf[Int]))
      case _ => None
    }

}

@JSExportTopLevel("Actions")
object Actions {

  import ActionUtils._

  @JSExport
  def addItem(name: String) = toJs(AddItem(name))

  @JSExport
  def deleteItem(index: Int) = toJs(DeleteItem(index))
}