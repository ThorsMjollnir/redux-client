package uk.ac.ncl.openlab.intake24.redux.api

import io.circe.generic.auto._
import uk.ac.ncl.openlab.intake24.redux.macros._

import uk.ac.ncl.openlab.intake24.api.client.roshttp.common.SigninImpl
import uk.ac.ncl.openlab.intake24.api.data.{EmailCredentials, RefreshResult}
import uk.ac.ncl.openlab.intake24.redux._

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("Client")
class Client(val reduxStore: Store, protected val selector: js.Array[String]) extends
  ModuleStore[ClientState, ClientAction] with ApiUtils {

  val requestHandler = new RequestHandlerImpl(() => {
    getScalaState().apiBaseUrl match {
      case Some(url) => url
      case None => throw new RuntimeException("API base URL is not set!!!")
    }
  })

  private var refreshListeners = List[RefreshSuccessful => Unit]()

  val authRequestHandler = new AuthRequestHandlerImpl(requestHandler, this)

  private val authService = new SigninImpl(requestHandler)

  def subscribeToRefresh(listener: RefreshSuccessful => Unit) = {
    refreshListeners ::= listener
  }

  @JSExport
  def init(apiBaseUrl: String) = dispatch(Init(apiBaseUrl))

  @JSExport
  def setRefreshToken(token: String) = {
    dispatch(SetRefreshToken(token))
    refreshAccessToken()
  }

  @JSExport
  def signin(email: String, password: String): Unit = {
    onComplete(authService.signin(EmailCredentials(email, password))) {
      case Right(signinResult) =>
        dispatch(SigninSuccessful(signinResult.refreshToken))
        refreshAccessToken()
      case Left(errorMessage) => dispatch(SigninFailed(errorMessage))
    }

    dispatch(SigninPending)
  }

  @JSExport
  def refreshAccessToken(): Unit = {
    getScalaState().refreshToken match {
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
}
