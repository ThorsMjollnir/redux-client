package uk.ac.ncl.openlab.intake24.redux.portionsize

sealed trait MethodSelectorAction

case class MethodSelected(index: Int) extends MethodSelectorAction
