package uk.ac.ncl.openlab.intake24.redux.auth

import uk.ac.ncl.openlab.intake24.api.data.EmailCredentials
import uk.ac.ncl.openlab.intake24.redux.Reducer

case class AuthenticationState(refreshToken: Option[String],
                               accessToken: Option[String],
                               credentials: Option[EmailCredentials],
                               error: Option[String])

object AuthenticationReducer extends Reducer[AuthenticationState, AuthenticationAction] {

  val initialState: AuthenticationState = AuthenticationState(None, None, None, None)

  def reducerImpl(previousState: AuthenticationState, action: AuthenticationAction): AuthenticationState = action match {
    case Init =>
      previousState
    case Signin(credentials) =>
      previousState.copy(credentials = Some(credentials))
    case DeleteRefreshToken =>
      previousState.copy(refreshToken = None)
    case DeleteAccessToken =>
      previousState.copy(accessToken = None)
    case SetRefreshToken(token) =>
      previousState.copy(refreshToken = Some(token))
    case SetAccessToken(token) =>
      previousState.copy(accessToken = Some(token))
  }
}
