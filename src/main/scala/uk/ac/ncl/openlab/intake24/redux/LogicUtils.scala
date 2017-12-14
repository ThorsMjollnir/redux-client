package uk.ac.ncl.openlab.intake24.redux

import shapeless.T
import uk.ac.ncl.openlab.intake24.api.client.ApiError
import uk.ac.ncl.openlab.intake24.api.client.ApiError.{HttpError, NetworkError}

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

trait LogicUtils {

  def handleApiResult(result: Future[Either[ApiError, T]], onSuccess: T => Unit, onError: String => Unit) = result.onComplete {
    case Success(Right(value)) => onSuccess(value)
    case Success(Left(NetworkError(throwable))) => onError(throwable.getMessage)
    case Success(Left(HttpError(_, Some(errorDescription)))) => onError(errorDescription.errorMessage)
    case Success(Left(HttpError(httpCode, None))) => onError(s"HTTP error: $httpCode")
    case Failure(throwable) => onError(s"Unexpected error: " + throwable.getMessage)
  }
}
