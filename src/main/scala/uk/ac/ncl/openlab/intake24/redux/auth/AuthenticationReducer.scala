package uk.ac.ncl.openlab.intake24.redux.auth

import uk.ac.ncl.openlab.intake24.api.data.EmailCredentials
import uk.ac.ncl.openlab.intake24.redux.{Macros, Reducer}

import scala.scalajs.js

case class AuthenticationState(refreshToken: Option[String],
                               accessToken: Option[String],
                               credentials: EmailCredentials,
                               signinClicked: Boolean,
                               signinRequestSent: Boolean,
                               errorMessage: Option[String])

object AuthenticationReducer extends Reducer[AuthenticationState, AuthenticationAction] {

  val initialState: AuthenticationState = AuthenticationState(None, None, EmailCredentials("", ""), false, false, None)

  def reducerImpl(previousState: AuthenticationState, action: AuthenticationAction): AuthenticationState = action match {
    case Init =>
      previousState

    case SigninClicked(credentials) =>
      previousState.copy(signinClicked = true, credentials = credentials, refreshToken = None, accessToken = None)

    case SigninSuccessful(refreshToken) =>
      previousState.copy(refreshToken = Some(refreshToken), signinClicked = false, signinRequestSent = false, errorMessage = None)

    case SigninFailed(errorMessage) =>
      previousState.copy(signinClicked = false, signinRequestSent = false, errorMessage = Some(errorMessage))

    case SigninRequestSent =>
      previousState.copy(signinRequestSent = true)
  }

  def actionFromJs(action: js.Dynamic): Option[AuthenticationAction] = Macros.actionFromJs[AuthenticationAction](action)
}
