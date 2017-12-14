package uk.ac.ncl.openlab.intake24.redux.auth

import uk.ac.ncl.openlab.intake24.api.data.EmailCredentials
import uk.ac.ncl.openlab.intake24.redux.{ModuleStore, Store}

import scala.scalajs.js.annotation.JSExport


class AuthenticationStore(val reduxStore: Store, val selector: Seq[String]) extends ModuleStore[AuthenticationState, AuthenticationAction] {

  @JSExport
  def signin(email: String, password: String) = dispatch(Signin(EmailCredentials(email, password)))

  @JSExport
  def refreshAccessToken() = dispatch(DeleteAccessToken)
}
