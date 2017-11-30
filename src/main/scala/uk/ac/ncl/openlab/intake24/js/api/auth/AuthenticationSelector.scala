package uk.ac.ncl.openlab.intake24.js.api.auth

import uk.ac.ncl.openlab.intake24.js.redux.{Store, TopLevelSelector}

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSExport
import js.JSConverters._

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.circe.scalajs.convertJsonToJs

class AuthenticationSelector(topLevelSelector: TopLevelSelector) {

  def authState: AuthenticationState = topLevelSelector.appState.auth

  @JSExport
  def get(): js.Any = convertJsonToJs(authState.asJson)

  @JSExport
  def shouldShowLogin(): Boolean = authState.refreshToken.isEmpty

  def shouldAttemptLogin() = authState.refreshToken.isEmpty && authState.password.nonEmpty && authState.userName.nonEmpty

  def shouldAttemptRefresh() = authState.accessToken.isEmpty && authState.refreshToken.nonEmpty

}
