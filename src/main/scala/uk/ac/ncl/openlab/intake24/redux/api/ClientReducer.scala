package uk.ac.ncl.openlab.intake24.redux.api

import uk.ac.ncl.openlab.intake24.redux.Reducer

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

import io.circe.generic.auto._
import uk.ac.ncl.openlab.intake24.redux.macros._

case class ClientState(apiBaseUrl: Option[String],
                       refreshToken: Option[String],
                       accessToken: Option[String],
                       signinRequestPending: Boolean,
                       errors: Seq[String])

@JSExportTopLevel("ClientReducer")
object ClientReducer extends Reducer[ClientState, ClientAction] {

  @JSExport
  val key = "client"

  val initialState: ClientState = ClientState(None, None, None, false, Seq())

  def reducerImpl(previousState: ClientState, action: ClientAction): ClientState = action match {
    case SetApiBaseUrl(apiBaseUrl: String) =>
      previousState.copy(apiBaseUrl = Some(apiBaseUrl))

    case SigninSuccessful(refreshToken) =>
      previousState.copy(refreshToken = Some(refreshToken), signinRequestPending = false)

    case SigninFailed(errorMessage) =>
      previousState.copy(refreshToken = None, signinRequestPending = false, errors = errorMessage +: previousState.errors)

    case SigninPending =>
      previousState.copy(signinRequestPending = true)

    case RefreshSuccessful(accessToken) =>
      previousState.copy(accessToken = Some(accessToken))

    case RefreshFailed(errorMessage) =>
      previousState.copy(accessToken = None, errors = errorMessage +: previousState.errors)

    case SetRefreshToken(refreshToken) =>
      previousState.copy(refreshToken = Some(refreshToken))

    case DeleteAccessToken =>
      previousState.copy(accessToken = None)

    case DeleteRefreshToken =>
      previousState.copy(refreshToken = None)
  }

}
