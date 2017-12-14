package uk.ac.ncl.openlab.intake24.redux

trait ReduxModule[S, A] extends Reducer[S, A] {
  def createSelector()
}
