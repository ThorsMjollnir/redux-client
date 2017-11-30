package uk.ac.ncl.openlab.intake24.js.api.auth

case class AuthenticationState(
                                refreshToken: Option[String] = None,
                                accessToken: Option[String] = None,
                                userName: Option[String] = None,
                                password: Option[String] = None,
                                errorCode: Option[Int] = None,
                              )
