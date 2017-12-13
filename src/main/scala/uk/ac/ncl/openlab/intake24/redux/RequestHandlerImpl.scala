package uk.ac.ncl.openlab.intake24.redux

import java.io.IOException

import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.exceptions.HttpException
import fr.hmil.roshttp.response.SimpleHttpResponse
import io.circe.Decoder
import uk.ac.ncl.openlab.intake24.api.client.{ApiError, JsonCodecs}
import uk.ac.ncl.openlab.intake24.api.client.ApiError.NetworkError
import uk.ac.ncl.openlab.intake24.api.client.services.RequestHandler
import uk.ac.ncl.openlab.intake24.api.data.ErrorDescription
import uk.ac.ncl.openlab.intake24.redux.auth.{AuthenticationDispatcher, AuthenticationSelector}

import scala.concurrent.{Future, Promise}

class RequestHandlerImpl(val apiBaseUrl: String, authSelector: AuthenticationSelector, authDispatcher: AuthenticationDispatcher)
  extends RequestHandler with JsonCodecs {

  authSelector.subscribe {
    state =>
      state.accessToken.foreach {
        token =>
          accessRetryQueue.foreach(_ (token))
          accessRetryQueue = List()
      }

      state.refreshToken.foreach {
        token =>
          refreshRetryQueue.foreach(_ (token))
          refreshRetryQueue = List()
      }
  }

  private var accessRetryQueue: List[String => Unit] = List()
  private var refreshRetryQueue: List[String => Unit] = List()

  lazy val apiBaseUrlNoSlash = apiBaseUrl.replaceAll("/+$", "")

  private def getUrl(endpoint: String) = apiBaseUrlNoSlash + "/" + endpoint.replaceAll("^/", "")

  private def decodeError[T](response: SimpleHttpResponse): Either[ApiError, T] = response.statusCode match {
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

  private def toApiError[T]: PartialFunction[Throwable, Future[Either[ApiError, T]]] = {
    case HttpException(e: SimpleHttpResponse) =>
      Future.successful(decodeError(e))
    case e: IOException =>
      Future.successful(Left(NetworkError(e)))
  }

  private def recoverRequestWithAccessToken[T](retry: HttpRequest): PartialFunction[Throwable, Future[Either[ApiError, T]]] = {
    case HttpException(e: SimpleHttpResponse) if e.statusCode == 401 =>

      val promise = Promise[Either[ApiError, T]]()

      accessRetryQueue +:= (token => {
        sendWithAccessToken(retry).onComplete(promise.complete(_))
      })

      authDispatcher.refreshAccessToken()

      promise.future
    case other =>
      toApiError(other)
  }

  private def recoverRequestWithRefreshToken[T](retry: HttpRequest): PartialFunction[Throwable, Future[Either[ApiError, T]]] = {
    case HttpException(e: SimpleHttpResponse) if e.statusCode == 401 =>
      val promise = Promise[Either[ApiError, T]]()

      accessRetryQueue +:= (token => {
        sendWithRefreshToken(retry).onComplete(promise.complete(_))
      })

      authDispatcher.refreshAccessToken()

      promise.future
    case other => toApiError(other)
  }

  def send[T](request: HttpRequest)(implicit decoder: Decoder[T]): Future[Either[ApiError, T]] =
    request.withURL(getUrl(request.url))
      .send()
      .map(decodeResponseBody[T])
      .recoverWith(toApiError)

  private def sendWithAuthToken[T](request: HttpRequest, authToken: Option[String],
                                   recover: PartialFunction[Throwable, Future[Either[ApiError, T]]]) =
    authToken.map(t => request.withHeader("X-Auth-Token", t)).getOrElse(request)
      .withURL(getUrl(request.url))
      .send()
      .map(decodeResponseBody[T])
      .recoverWith(recover)

  def sendWithAccessToken[T](request: HttpRequest)(implicit decoder: Decoder[T]): Future[Either[ApiError, T]] =
    sendWithAuthToken(request, authSelector.getState.accessToken, recoverRequestWithAccessToken(request))

  def sendWithRefreshToken[T](request: HttpRequest)(implicit decoder: Decoder[T]): Future[Either[ApiError, T]] =
    sendWithAuthToken(request, authSelector.getState.refreshToken, recoverRequestWithRefreshToken(request))

}
