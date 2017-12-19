package uk.ac.ncl.openlab.intake24.redux

import java.io.IOException

import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.exceptions.HttpException
import fr.hmil.roshttp.response.SimpleHttpResponse
import io.circe.Decoder
import io.circe.generic.auto._
import monix.execution.Scheduler.Implicits.global
import uk.ac.ncl.openlab.intake24.api.client.ApiError.NetworkError
import uk.ac.ncl.openlab.intake24.api.client.services.AuthRequestHandler
import uk.ac.ncl.openlab.intake24.api.client.{ApiError, JsonCodecs}
import uk.ac.ncl.openlab.intake24.api.data.ErrorDescription
import uk.ac.ncl.openlab.intake24.redux.api.{DeleteAccessToken, DeleteRefreshToken}

import scala.concurrent.{Future, Promise}

trait RequestUtils extends JsonCodecs {

  val apiBaseUrl: String

  lazy val apiBaseUrlNoSlash = apiBaseUrl.replaceAll("/+$", "")

  protected def getUrl(endpoint: String) = apiBaseUrlNoSlash + "/" + endpoint.replaceAll("^/", "")

  protected def decodeError[T](response: SimpleHttpResponse): Either[ApiError, T] = response.statusCode match {
    case 401 => Left(ApiError.HttpError(401, None))
    case 403 => Left(ApiError.HttpError(403, None))
    case code =>
      fromJson[ErrorDescription](response.body) match {
        case Right(errorDescription) => Left(ApiError.HttpError(code, Some(errorDescription)))
        case Left(e) => Left(ApiError.HttpError(code, None))
      }
  }

  protected def decodeResponseBody[T](response: SimpleHttpResponse)(implicit decoder: Decoder[T]): Either[ApiError, T] =
    fromJson[T](response.body) match {
      case Right(result) => Right(result)
      case Left(e) => Left(ApiError.ResultParseFailed(e))
    }

  protected def toApiError[T]: PartialFunction[Throwable, Future[Either[ApiError, T]]] = {
    case HttpException(e: SimpleHttpResponse) =>
      Future.successful(decodeError(e))
    case e: IOException =>
      Future.successful(Left(NetworkError(e)))
  }
}
