package com.github.cupenya.microservices.sdk

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directives, RejectionHandler}
import com.github.cupenya.microservices.sdk.authentication.AuthorizationDirectives
import com.github.cupenya.microservices.sdk.logging.Logging
import spray.json._

import scala.concurrent.ExecutionContext

case class AuthError(error: String)

trait SampleHttpService extends Directives with AuthorizationDirectives with SprayJsonSupport with DefaultJsonProtocol with Logging {
  implicit val ec: ExecutionContext

  implicit val authErrorFormat = jsonFormat1(AuthError)

  private val rh = RejectionHandler.newBuilder().handle {
    case AuthorizationFailedRejection =>
      complete(Forbidden -> AuthError("The supplied authentication is not authorized to access this resource"))
  }.result()

  val routes = handleRejections(rh) {
    authorized { authInfo =>
      pathPrefix("test") {
        get {
          complete(OK, None)
        }
      }
    }
  }
}
