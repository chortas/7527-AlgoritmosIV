package edu.fiuba.fpfiuba43.models

import java.time.LocalDateTime

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class InputRow(id: Int,
                    date: LocalDateTime,
                    open: Option[Double],
                    high: Option[Double],
                    low: Option[Double],
                    last: Double,
                    close: Double,
                    diff: Double,
                    curr: String,
                    oVol: Option[Int],
                    oDiff: Option[Int],
                    opVol: Option[Int],
                    unit: String,
                    dollarBN: Double,
                    dollarItau: Double,
                    wDiff: Double)

// TODO hash_code vs hashCode
case class ScoresRow(hash_code: Int,
                     score: Double)

object InputRow {
  implicit val decoder: Decoder[InputRow] = deriveDecoder
}
