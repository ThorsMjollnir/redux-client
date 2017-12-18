package uk.ac.ncl.openlab.intake24.redux.auth

sealed trait AuthenticationAction

case object Init extends AuthenticationAction


case class SigninSuccessful(refreshToken: String) extends AuthenticationAction

case class SigninFailed(errorMessage: String) extends AuthenticationAction

case object SigninPending extends AuthenticationAction

case class RefreshSuccessful(accessToken: String) extends AuthenticationAction

case class RefreshFailed(errorMessage: String) extends AuthenticationAction

case object DeleteAccessToken extends AuthenticationAction

case object DeleteRefreshToken extends AuthenticationAction


//case class SetRefreshToken(token: String) extends AuthenticationAction

//case class SetAccessToken(token: String) extends AuthenticationAction

