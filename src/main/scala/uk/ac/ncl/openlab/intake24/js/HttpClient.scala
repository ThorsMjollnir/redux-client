package uk.ac.ncl.openlab.intake24.js

import scala.scalajs.js

@js.native
trait HttpClient extends js.Object {
  def sendRequest(method: String, url: String, body: String, onComplete: js.Function, onError: js.Function)

}
