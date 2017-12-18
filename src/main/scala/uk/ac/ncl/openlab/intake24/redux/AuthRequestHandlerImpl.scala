package uk.ac.ncl.openlab.intake24.redux

import fr.hmil.roshttp.HttpRequest
import io.circe.Decoder
import monix.execution.Scheduler.Implicits.global
import uk.ac.ncl.openlab.intake24.api.client.ApiError.HttpError
import uk.ac.ncl.openlab.intake24.api.client.services.common.AuthService
import uk.ac.ncl.openlab.intake24.api.client.services.{AuthRequestHandler, RequestHandler}
import uk.ac.ncl.openlab.intake24.api.client.{ApiError, JsonCodecs}
import uk.ac.ncl.openlab.intake24.redux.auth.{AuthenticationStore, RefreshSuccessful}

import scala.concurrent.{Future, Promise}
import scala.util.Success

class AuthRequestHandlerImpl(val requestHandler: RequestHandler, authStore: AuthenticationStore)
  extends AuthRequestHandler with JsonCodecs {

  var retryList = List[RefreshSuccessful => Unit]()

  def retry(event: RefreshSuccessful) = {
    val r = retryList
    r.foreach(_ (event))
  }

  authStore.subscribe((event: RefreshSuccessful) => retry(event))

  def sendWithAccessToken[T](request: HttpRequest)(implicit decoder: Decoder[T]): Future[Either[ApiError, T]] = {
    val result = Promise[Either[ApiError, T]]()

    requestHandler.send(authStore.getState().accessToken.map(t => request.withHeader("X-Auth-Token", t))
      .getOrElse(request))(decoder).onComplete {
      case Success(Left(HttpError(401, _))) =>
        retryList +:= ((event: RefreshSuccessful) => {
          sendWithAccessToken(request)(decoder).onComplete(result.complete(_))
        })
      case other => result.complete(other)
    }

    result.future
  }
}
