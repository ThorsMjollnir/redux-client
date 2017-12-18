package uk.ac.ncl.openlab.intake24.redux

import uk.ac.ncl.openlab.intake24.api.client.ApiError
import uk.ac.ncl.openlab.intake24.api.client.ApiError.{HttpError, NetworkError}

import scala.concurrent.Future
import scala.util.{Failure, Success}

import monix.execution.Scheduler.Implicits.global

trait ApiUtils {

  def onComplete[T](result: Future[Either[ApiError, T]])(f: Either[String, T] => Unit) = result.onComplete {
    result =>

      val simpleResult = result match {
        case Success(result) =>
          result.left.map {
            case NetworkError(throwable) => throwable.getMessage
            case HttpError(_, Some(errorDescription)) => errorDescription.errorMessage
            case HttpError(httpCode, None) => s"HTTP error: $httpCode"
          }
        case Failure(throwable) => Left(s"Unexpected error: " + throwable.getMessage)
      }

      f(simpleResult)
  }
}
