package uk.ac.ncl.openlab.intake24.redux.auth

import uk.ac.ncl.openlab.intake24.api.client.services.common.AuthService
import uk.ac.ncl.openlab.intake24.redux.ApiUtils

object AuthenticationLogic extends ApiUtils {

  var authService: AuthService = null
  var store: AuthenticationStore = null

  def onStateChanged(s: AuthenticationState): Unit = {

    if (s.signinClicked && !s.signinRequestSent) {
      handleApiResult(authService.signin(s.credentials))(
        result => store.dispatch(SigninSuccessful(result.refreshToken)),
        errorMessage => store.dispatch(SigninFailed(errorMessage)))

      store.dispatch(SigninRequestSent)
    }
  }

  def init(store: AuthenticationStore, authService: AuthService): Unit = {
    this.authService = authService
    this.store = store
    store.subscribe(onStateChanged)
    store.dispatch(Init)
  }
}
