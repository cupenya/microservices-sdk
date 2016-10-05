package com.github.cupenya.microservices.sdk.authentication

case class AuthInfo(
  authKey: String,
  userId: String,
  permissions: List[String],
  groupIds: List[String],
  expirationDate: Option[Long]
)
