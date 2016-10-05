package com.github.cupenya.microservices.sdk.authentication

import com.github.cupenya.microservices.sdk.logging.Logging
import com.typesafe.config.ConfigFactory
import pdi.jwt.{Jwt, JwtAlgorithm}

import scala.concurrent.{ExecutionContext, Future}
import spray.json._

trait TokenVerifier[T] {
  def verifyToken(token: String)(implicit ec: ExecutionContext): Future[T]
}

class JwtTokenVerifier extends TokenVerifier[AuthInfo] with DefaultJsonProtocol with Logging {

  implicit val authInfoFormat = jsonFormat5(AuthInfo)

  val config = ConfigFactory.load()

  override def verifyToken(token: String)(implicit ec: ExecutionContext): Future[AuthInfo] = Future {
    Jwt.decode(token, config.getString("authentication.secret"), Seq(JwtAlgorithm.HS256)).map { decodedJson =>
      log.debug(s"Decoded json token $decodedJson")
      decodedJson.parseJson.convertTo[AuthInfo]
    }.get
  }
}
