package uk.ac.ncl.openlab.intake24.redux

import io.circe._
import io.circe.syntax._
import io.circe.scalajs._

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSExport

abstract class Reducer[S, A](implicit stateDecoder: Decoder[S], stateEncoder: Encoder[S], actionDecoder: Decoder[A]) {

  def initialState: S

  def reducerImpl(previousState: S, action: A): S

  val reduxActionDecoder = new ReduxSumTypeDecoder[A](Reducer.actionTypePrefix)

  def decodeJsAction(action: js.Any): Either[Throwable, A] =
    decodeJs(action)(reduxActionDecoder)

  @JSExport
  def create(): js.Function =
    (previousState: UndefOr[js.Any], action: js.Any) =>
      if (previousState.isEmpty)
        initialState.asJsAny
      else
        decodeJs[A](action)(reduxActionDecoder) match {
          case Right(scalaAction) =>
            val scalaState = decodeJs[S](previousState.get).right.get
            reducerImpl(scalaState, scalaAction).asJsAny
          case Left(_) =>
            previousState
        }
}

object Reducer {
  val actionTypePrefix = "intake24."
}