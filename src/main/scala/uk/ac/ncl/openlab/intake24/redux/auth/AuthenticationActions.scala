package uk.ac.ncl.openlab.intake24.redux.auth

import uk.ac.ncl.openlab.intake24.api.data.EmailCredentials

sealed trait AuthenticationAction

case object Init extends AuthenticationAction

case class Signin(credentials: EmailCredentials) extends AuthenticationAction

case object DeleteAccessToken extends AuthenticationAction

case object DeleteRefreshToken extends AuthenticationAction

case object DeleteCredentials extends AuthenticationAction

case class SetRefreshToken(token: String) extends AuthenticationAction

case class SetAccessToken(token: String) extends AuthenticationAction

case class SetError(message: String) extends AuthenticationAction

