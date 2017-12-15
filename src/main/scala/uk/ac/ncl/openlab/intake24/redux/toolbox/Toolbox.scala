package uk.ac.ncl.openlab.intake24.redux.toolbox

import uk.ac.ncl.openlab.intake24.api.client.roshttp.common.SigninImpl
import uk.ac.ncl.openlab.intake24.redux.auth.{AuthenticationLogic, AuthenticationReducer, AuthenticationStore}
import uk.ac.ncl.openlab.intake24.redux.{Redux, Store}

import scala.scalajs.js
import scala.scalajs.js.Dictionary
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

import io.circe.generic.auto._

@JSExportTopLevel("Toolbox")
object Toolbox {

  @JSExport
  def createReducer(): js.Function = {
    Redux.combineReducers(Dictionary(
      "auth" -> AuthenticationReducer.createReducer()
    ))
  }

  @JSExport
  var authStore: AuthenticationStore = null

  @JSExport
  def init(apiBaseUrl: String, reduxStore: Store, namespace: String): ToolboxStore = {

    authStore = new AuthenticationStore(reduxStore, Seq(namespace, "auth"))

    val signinService = new SigninImpl(apiBaseUrl)

    AuthenticationLogic.init(authStore, signinService)

    new ToolboxStore(reduxStore, Seq(namespace))
  }
}
