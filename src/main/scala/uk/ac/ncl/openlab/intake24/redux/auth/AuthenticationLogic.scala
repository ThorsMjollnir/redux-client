package uk.ac.ncl.openlab.intake24.redux.auth

import uk.ac.ncl.openlab.intake24.api.client.ApiError
import uk.ac.ncl.openlab.intake24.api.client.ApiError.{HttpError, NetworkError}
import uk.ac.ncl.openlab.intake24.api.client.services.common.AuthService
import uk.ac.ncl.openlab.intake24.redux.LogicUtils

import scala.util.{Failure, Success}

object AuthenticationLogic extends LogicUtils {

  var authService: AuthService = null
  var store: AuthenticationStore = null

  def onStateChanged(authenticationState: AuthenticationState): Unit = {


    authenticationState.credentials match {
      case Some(credentials) =>
        handleApiResult(authService.signin(credentials), r => store.dispatch(SetRefreshToken(r.refreshToken))

          .onComplete(handleApiResult())
          case Success(Right(signinResult)) =>

          case Success(Left(NetworkError(throwable))) =>
            store.dispatch(SetError(throwable.getMessage))
          case Success(Left(HttpError(_, Some(errorDescription)))) =>
            store.dispatch(SetError(errorDescription.errorMessage))
          case Success(Left(HttpError(httpCode, None))) =>
            store.dispatch(SetError(s"HTTP error: $httpCode"))
          case Failure(throwable) =>
            store.dispatch(SetError(s"Unexpected error: " + throwable.getMessage))
        }

        store.dispatch(DeleteCredentials)
    }



  }

  def init(store: AuthenticationStore, authService: AuthService): Unit = {
    this.authService = authService
    this.store = store
    store.subscribe(onStateChanged)
    store.dispatch(Init)
  }
}
