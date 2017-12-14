package uk.ac.ncl.openlab.intake24.redux

trait ModuleStore[S, A] {

  val reduxStore: Store

  val selector: Seq[String]

  def dispatch(action: A) = reduxStore.dispatch(actionToJs(action))

  def actionToJs(action: A) = Macros.actionToJs[A](action)

  def getState = selector.foldLeft(reduxStore.getState()) {
    (obj, path) => obj.selectDynamic(path)
  }.asInstanceOf[S]

  def subscribe(handler: S => Unit) =
    reduxStore.subscribe(() => {
      handler(getState)
    })
}
