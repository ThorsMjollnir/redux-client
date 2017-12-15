package uk.ac.ncl.openlab.intake24.redux.auth

import uk.ac.ncl.openlab.intake24.api.data.EmailCredentials
import uk.ac.ncl.openlab.intake24.redux.{Macros, ModuleStore, Store}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

import io.circe.scalajs._
import io.circe.generic.auto._

class AuthenticationStore(val reduxStore: Store, val selector: Seq[String]) extends ModuleStore[AuthenticationState, AuthenticationAction] {

  @JSExport
  def signin(email: String, password: String) = dispatch(SigninClicked(EmailCredentials(email, password)))

  def actionToJs(action: AuthenticationAction): js.Any = Macros.actionToJs[AuthenticationAction](action)

  def stateFromJs(state: js.Any): AuthenticationState = decodeJs[AuthenticationState](state).right.get
}
