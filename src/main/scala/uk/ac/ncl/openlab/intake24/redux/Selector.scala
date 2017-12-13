package uk.ac.ncl.openlab.intake24.redux

trait Selector[T] {

  val store: Store
  val namespace: String

  def getState = store.getState().selectDynamic(namespace).asInstanceOf[T]

  def subscribe(handler: T => Unit) =
    store.subscribe(() => {
      handler(getState)
    })
}
