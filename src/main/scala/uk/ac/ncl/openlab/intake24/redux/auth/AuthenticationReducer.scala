package uk.ac.ncl.openlab.intake24.redux.auth

import uk.ac.ncl.openlab.intake24.redux.{Macros, Reducer}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

case class AuthenticationState(refreshToken: Option[String],
                               accessToken: Option[String],
                               signinRequestPending: Boolean,
                               errors: Seq[String])

@JSExportTopLevel("AuthenticationReducer")
object AuthenticationReducer extends Reducer[AuthenticationState, AuthenticationAction] {

  @JSExport
  val key = "auth"

  val initialState: AuthenticationState = AuthenticationState(None, None, false, Seq())

  def reducerImpl(previousState: AuthenticationState, action: AuthenticationAction): AuthenticationState = action match {
    case Init =>
      previousState

    case SigninSuccessful(refreshToken) =>
      previousState.copy(refreshToken = Some(refreshToken), signinRequestPending = false)

    case SigninFailed(errorMessage) =>
      previousState.copy(refreshToken = None, signinRequestPending = false, errors = errorMessage +: previousState.errors)

    case SigninPending =>
      previousState.copy(signinRequestPending = true)

    case RefreshSuccessful(accessToken) =>
      previousState.copy(accessToken = Some(accessToken))

    case RefreshFailed(errorMessage) =>
      previousState.copy(accessToken = None, errors = errorMessage +: previousState.errors)
  }

  def actionFromJs(action: js.Dynamic): Option[AuthenticationAction] = Macros.actionFromJs[AuthenticationAction](action)
}
