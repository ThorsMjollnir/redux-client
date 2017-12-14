package uk.ac.ncl.openlab.intake24.redux

trait Selector[T] {

  val store: Store
  val path: Seq[String]


}
