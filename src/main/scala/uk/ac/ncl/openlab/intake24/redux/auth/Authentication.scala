package uk.ac.ncl.openlab.intake24.redux.auth

import uk.ac.ncl.openlab.intake24.redux.Store

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}


case class AuthenticationState(
                                refreshToken: Option[String] = None,
                                accessToken: Option[String] = None,
                                userName: Option[String] = None,
                                password: Option[String] = None,
                                errorCode: Option[Int] = None
                              )

@JSExportTopLevel("Authentication")
object Authentication {
  @JSExport
  val actions = AuthenticationDispatcher

  private def reducerImpl(previousState: AuthenticationState, action: AuthenticationAction): AuthenticationState = action match {
    case Init =>
      previousState
  }



  private def onStateChanged(store: Store, state: AuthenticationState): Unit = {

  }

  @JSExport
  def init(store: Store, namespace: String): AuthenticationSelector = {
    val selector = new AuthenticationSelector(store, namespace)
    store.subscribe(() => onStateChanged(store, selector.getState))
    selector
  }
}