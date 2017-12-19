package uk.ac.ncl.openlab.intake24.redux.api

sealed trait ClientAction

case object DeleteRefreshToken extends ClientAction

case object DeleteAccessToken extends ClientAction

case object SigninPending extends ClientAction

case class Init(apiBaseUrl: String) extends ClientAction

case class SigninSuccessful(refreshToken: String) extends ClientAction

case class SigninFailed(errorMessage: String) extends ClientAction

case class RefreshSuccessful(accessToken: String) extends ClientAction

case class RefreshFailed(errorMessage: String) extends ClientAction

case class SetRefreshToken(refreshToken: String) extends ClientAction
