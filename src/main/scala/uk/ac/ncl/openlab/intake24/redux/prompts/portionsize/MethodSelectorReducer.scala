package uk.ac.ncl.openlab.intake24.redux.prompts.portionsize

import io.circe.generic.auto._
import uk.ac.ncl.openlab.intake24.api.data.PortionSizeMethodForSurvey
import uk.ac.ncl.openlab.intake24.redux.Reducer
import uk.ac.ncl.openlab.intake24.redux.portionsize.{MethodSelected, MethodSelectorAction}

import scala.scalajs.js.annotation.JSExportTopLevel

case class MethodSelectorState(availableMethods: IndexedSeq[PortionSizeMethodForSurvey], selectedIndex: Option[Int])

@JSExportTopLevel("MethodSelectorReducer")
object MethodSelectorReducer extends Reducer[MethodSelectorState, MethodSelectorAction] {

  val initialState = MethodSelectorState(IndexedSeq(), None)

  def reducerImpl(previousState: MethodSelectorState, action: MethodSelectorAction): MethodSelectorState = action match {
    case MethodSelected(index) => previousState.copy(selectedIndex = Some(index))
  }
}
