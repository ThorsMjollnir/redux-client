package uk.ac.ncl.openlab.intake24.redux.auth

import uk.ac.ncl.openlab.intake24.api.data.{EmailCredentials, RefreshResult}
import uk.ac.ncl.openlab.intake24.redux.{ApiUtils, Macros, ModuleStore, Store}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import io.circe.scalajs._
import io.circe.generic.auto._
import uk.ac.ncl.openlab.intake24.api.client.services.common.AuthService

class AuthenticationStore(val reduxStore: Store, val selector: Seq[String], authService: AuthService) extends
  ModuleStore[AuthenticationState, AuthenticationAction] with ApiUtils {

  var refreshListeners = List[RefreshSuccessful => Unit]()

  def subscribe(listener: RefreshSuccessful => Unit) = refreshListeners ::= listener

  @JSExport
  def signin(email: String, password: String): Unit = {
    onComplete(authService.signin(EmailCredentials(email, password))) {
      case Right(signinResult) =>
        dispatch(SigninSuccessful(signinResult.refreshToken))
        refresh()
      case Left(errorMessage) => dispatch(SigninFailed(errorMessage))
    }

    dispatch(SigninPending)
  }

  @JSExport
  def refresh(): Unit = {
    getState().refreshToken match {
      case Some(token) =>
        onComplete[RefreshResult](authService.refresh(token)) {
          case Right(refreshResult) =>
            val event = RefreshSuccessful(refreshResult.accessToken)
            dispatch(event)
            refreshListeners.foreach(_ (event))

          case Left(errorMessage) =>
            dispatch(RefreshFailed(errorMessage))
        }
      case None =>
        dispatch(RefreshFailed("Can't refresh without a refresh token, signin first"))
    }
  }

  def updateAccessToken(accessToken: String): Unit = dispatch(RefreshSuccessful(accessToken))

  def actionToJs(action: AuthenticationAction): js.Any = Macros.actionToJs[AuthenticationAction](action)

  def stateFromJs(state: js.Any): AuthenticationState = decodeJs[AuthenticationState](state).right.get
}
