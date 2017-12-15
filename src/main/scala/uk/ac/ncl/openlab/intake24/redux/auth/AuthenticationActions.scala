package uk.ac.ncl.openlab.intake24.redux.auth

import uk.ac.ncl.openlab.intake24.api.data.EmailCredentials

sealed trait AuthenticationAction

case object Init extends AuthenticationAction


case class SigninClicked(credentials: EmailCredentials) extends AuthenticationAction

case class SigninSuccessful(refreshToken: String) extends AuthenticationAction

case class SigninFailed(errorMessage: String) extends AuthenticationAction

case object SigninRequestSent extends AuthenticationAction



case object DeleteAccessToken extends AuthenticationAction

case object DeleteRefreshToken extends AuthenticationAction


//case class SetRefreshToken(token: String) extends AuthenticationAction

//case class SetAccessToken(token: String) extends AuthenticationAction

