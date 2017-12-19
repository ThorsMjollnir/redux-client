package uk.ac.ncl.openlab.intake24.redux

import io.circe.{Decoder, Encoder}
import io.circe.scalajs._

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSExport

abstract class Reducer[S, A](implicit stateDecoder: Decoder[S], stateEncoder: Encoder[S], actionDecoder: ActionDecoder[A]) {

  def initialState: S

  def reducerImpl(previousState: S, action: A): S

  @JSExport
  def create(): js.Function =
    (previousState: UndefOr[js.Dynamic], action: js.Dynamic) =>
      if (previousState.isEmpty)
        initialState.asJsAny
      else
        actionDecoder.decode(action) match {
          case Some(scalaAction) =>
            val scalaState = decodeJs[S](previousState.get).right.get
            reducerImpl(scalaState, scalaAction).asJsAny
          case None => previousState
        }
}
