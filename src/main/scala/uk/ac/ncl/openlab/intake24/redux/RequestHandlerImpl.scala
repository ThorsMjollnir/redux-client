package uk.ac.ncl.openlab.intake24.redux

import java.io.IOException

import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.exceptions.HttpException
import fr.hmil.roshttp.response.SimpleHttpResponse
import io.circe.Decoder
import io.circe.generic.auto._
import monix.execution.Scheduler.Implicits.global
import uk.ac.ncl.openlab.intake24.api.client.ApiError.NetworkError
import uk.ac.ncl.openlab.intake24.api.client.services.RequestHandler
import uk.ac.ncl.openlab.intake24.api.client.{ApiError, JsonCodecs}
import uk.ac.ncl.openlab.intake24.api.data.ErrorDescription

import scala.concurrent.Future

class RequestHandlerImpl(val apiBaseUrl: String) extends RequestHandler with JsonCodecs {

  lazy val apiBaseUrlNoSlash = apiBaseUrl.replaceAll("/+$", "")

  private def getUrl(endpoint: String) = apiBaseUrlNoSlash + "/" + endpoint.replaceAll("^/", "")

  private def decodeError[T](response: SimpleHttpResponse): Either[ApiError, T] =
    fromJson[ErrorDescription](response.body) match {
      case Right(errorDescription) => Left(ApiError.HttpError(response.statusCode, Some(errorDescription)))
      case Left(_) => Left(ApiError.HttpError(response.statusCode, None))
    }

  private def decodeResponseBody[T](response: SimpleHttpResponse)(implicit decoder: Decoder[T]): Either[ApiError, T] =
    fromJson[T](response.body) match {
      case Right(result) => Right(result)
      case Left(e) => Left(ApiError.ResultParseFailed(e))
    }

  private def toApiError[T]: PartialFunction[Throwable, Future[Either[ApiError, T]]] = {
    case HttpException(e: SimpleHttpResponse) =>
      Future.successful(decodeError(e))
    case e: IOException =>
      Future.successful(Left(NetworkError(e)))
  }

  def send[T](request: HttpRequest)(implicit decoder: Decoder[T]): Future[Either[ApiError, T]] = {
    request.withURL(getUrl(request.longPath))
      .send()
      .map(decodeResponseBody[T])
      .recoverWith(toApiError)
  }
}
