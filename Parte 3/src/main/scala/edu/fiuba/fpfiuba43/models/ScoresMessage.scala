package edu.fiuba.fpfiuba43.models

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

final case class ScoresMessage(score: Double)

object ScoresMessage {
  implicit val encoder: Encoder[ScoresMessage] = deriveEncoder
}
