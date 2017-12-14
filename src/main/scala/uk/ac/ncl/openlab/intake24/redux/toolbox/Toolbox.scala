package uk.ac.ncl.openlab.intake24.redux.toolbox

import uk.ac.ncl.openlab.intake24.api.client.roshttp.common.SigninImpl
import uk.ac.ncl.openlab.intake24.redux.auth.{AuthenticationReducer, AuthenticationStore}
import uk.ac.ncl.openlab.intake24.redux.{Redux, RequestHandlerImpl, Store}

import scala.scalajs.js
import scala.scalajs.js.Dictionary
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("Toolbox")
object Toolbox {

  @JSExport
  def createReducer(): js.Function = {
    Redux.combineReducers(Dictionary(
      "auth" -> AuthenticationReducer.createReducer()
    ))
  }

  @JSExport
  def init(apiBaseUrl: String, reduxStore: Store, namespace: String) = {

    val authStore = new AuthenticationStore(reduxStore, Seq(namespace, "auth"))

    val requestHandler = new RequestHandlerImpl(apiBaseUrl, authStore)

    val signinService = new SigninImpl(requestHandler)
  }
}
