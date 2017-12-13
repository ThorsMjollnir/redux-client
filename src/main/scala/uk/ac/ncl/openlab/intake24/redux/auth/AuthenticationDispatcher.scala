package uk.ac.ncl.openlab.intake24.redux.auth

import uk.ac.ncl.openlab.intake24.redux.{Dispatcher, Store}

import scala.scalajs.js.annotation.JSExport

sealed trait AuthenticationAction

case object Init extends AuthenticationAction

case class Signin(email: String, password: String) extends AuthenticationAction

case object RefreshAccessToken extends AuthenticationAction

class AuthenticationDispatcher(val store: Store) extends Dispatcher[AuthenticationAction] {

  @JSExport
  def signin(email: String, password: String) = store.dispatch(actionToJs(Signin(email, password)))

  @JSExport
  def refreshAccessToken() = store.dispatch(actionToJs(RefreshAccessToken))
}
