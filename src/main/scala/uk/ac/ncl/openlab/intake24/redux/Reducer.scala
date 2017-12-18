package uk.ac.ncl.openlab.intake24.redux

import io.circe.{Decoder, Encoder}

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSExport
import io.circe.scalajs._

trait Reducer[S, A] {

  def initialState: S

  def reducerImpl(previousState: S, action: A): S

  def actionFromJs(action: js.Dynamic): Option[A]

  @JSExport
  def create()(implicit encoder: Encoder[S], decoder: Decoder[S]): js.Function =
    (previousState: UndefOr[js.Dynamic], action: js.Dynamic) =>
      if (previousState.isEmpty)
        initialState.asJsAny
      else
        actionFromJs(action) match {
          case Some(scalaAction) =>
            val scalaState = decodeJs[S](previousState.get).right.get
            reducerImpl(scalaState, scalaAction).asJsAny
          case None => previousState
        }
}
