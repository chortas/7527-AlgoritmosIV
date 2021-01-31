package edu.fiuba.fpfiuba43.models

import io.circe._
import io.circe.generic.semiauto._

final case class HealthCheckMessage(version: String, maintainer: String)

object HealthCheckMessage {
  implicit val encoder: Encoder[HealthCheckMessage] = deriveEncoder
}