package uk.ac.ncl.openlab.intake24.redux

import java.io.IOException

import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.exceptions.HttpException
import fr.hmil.roshttp.response.SimpleHttpResponse
import io.circe.Decoder
import io.circe.generic.auto._
import uk.ac.ncl.openlab.intake24.api.client.{ApiError, JsonCodecs}
import uk.ac.ncl.openlab.intake24.api.client.ApiError.NetworkError
import uk.ac.ncl.openlab.intake24.api.client.services.RequestHandler
import uk.ac.ncl.openlab.intake24.api.data.ErrorDescription
import uk.ac.ncl.openlab.intake24.redux.auth.{AuthenticationStore, DeleteAccessToken, DeleteRefreshToken}

import scala.concurrent.{Future, Promise}

import monix.execution.Scheduler.Implicits.global

class RequestHandlerImpl(val apiBaseUrl: String, authStore: AuthenticationStore)
  extends RequestHandler with JsonCodecs {

  authStore.subscribe {
    state =>
      state.accessToken.foreach {
        token =>
          accessRetryQueue.foreach(_ ())
          accessRetryQueue = List()
      }

      state.refreshToken.foreach {
        token =>
          refreshRetryQueue.foreach(_ ())
          refreshRetryQueue = List()
      }
  }

  private var accessRetryQueue: List[() => Unit] = List()
  private var refreshRetryQueue: List[() => Unit] = List()

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

  private def recoverRequestWithAccessToken[T](retry: HttpRequest, bodyDecoder: Decoder[T]): PartialFunction[Throwable, Future[Either[ApiError, T]]] = {
    case HttpException(e: SimpleHttpResponse) if e.statusCode == 401 =>

      val promise = Promise[Either[ApiError, T]]()

      accessRetryQueue +:= (() => {
        sendWithAccessToken(retry)(bodyDecoder).onComplete(promise.complete(_))
      })

      authStore.dispatch(DeleteAccessToken)

      promise.future
    case other =>
      toApiError(other)
  }

  private def recoverRequestWithRefreshToken[T](retry: HttpRequest, bodyDecoder: Decoder[T]): PartialFunction[Throwable, Future[Either[ApiError, T]]] = {
    case HttpException(e: SimpleHttpResponse) if e.statusCode == 401 =>
      val promise = Promise[Either[ApiError, T]]()


      refreshRetryQueue +:= (() => {
        sendWithRefreshToken(retry)(bodyDecoder).onComplete(promise.complete(_))
      })

      authStore.dispatch(DeleteRefreshToken)

      promise.future
    case other => toApiError(other)
  }

  def send[T](request: HttpRequest)(implicit decoder: Decoder[T]): Future[Either[ApiError, T]] =
    request.withURL(getUrl(request.url))
      .send()
      .map(decodeResponseBody[T])
      .recoverWith(toApiError)

  private def sendWithAuthToken[T](request: HttpRequest, authToken: Option[String],
                                   recover: PartialFunction[Throwable, Future[Either[ApiError, T]]])(implicit decoder: Decoder[T]) =
    authToken.map(t => request.withHeader("X-Auth-Token", t)).getOrElse(request)
      .withURL(getUrl(request.url))
      .send()
      .map(decodeResponseBody[T])
      .recoverWith(recover)

  def sendWithAccessToken[T](request: HttpRequest)(implicit decoder: Decoder[T]): Future[Either[ApiError, T]] =
    sendWithAuthToken(request, authStore.getState.accessToken, recoverRequestWithAccessToken(request, decoder))

  def sendWithRefreshToken[T](request: HttpRequest)(implicit decoder: Decoder[T]): Future[Either[ApiError, T]] =
    sendWithAuthToken(request, authStore.getState.refreshToken, recoverRequestWithRefreshToken(request, decoder))

}
