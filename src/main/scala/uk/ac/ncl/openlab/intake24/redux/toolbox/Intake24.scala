package uk.ac.ncl.openlab.intake24.redux.toolbox

import uk.ac.ncl.openlab.intake24.api.client.roshttp.common.SigninImpl
import uk.ac.ncl.openlab.intake24.api.client.services.AuthRequestHandler
import uk.ac.ncl.openlab.intake24.redux.auth.{AuthenticationReducer, AuthenticationStore}
import uk.ac.ncl.openlab.intake24.redux.{AuthRequestHandlerImpl, RequestHandlerImpl, Store}

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("Intake24")
object Intake24 {

  @JSExport
  var auth: AuthenticationStore = null

  @JSExport
  var authRequestHandler: AuthRequestHandler = null

  @JSExport
  def init(apiBaseUrl: String, reduxStore: Store, namespace: String): Unit = {

    val requestHandler = new RequestHandlerImpl(apiBaseUrl)

    val signinService = new SigninImpl(requestHandler)

    auth = new AuthenticationStore(reduxStore, Seq(namespace, AuthenticationReducer.key), signinService)

    authRequestHandler = new AuthRequestHandlerImpl(requestHandler, auth)
  }
}
