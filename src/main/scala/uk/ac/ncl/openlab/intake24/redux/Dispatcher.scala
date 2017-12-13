package uk.ac.ncl.openlab.intake24.redux

trait Dispatcher[T] {
  def actionToJs(action: T) = Macros.actionToJs[T](action)
}
