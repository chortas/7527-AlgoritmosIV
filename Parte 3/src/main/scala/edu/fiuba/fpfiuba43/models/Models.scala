package edu.fiuba.fpfiuba43.models

import java.time.{LocalDateTime, ZoneOffset}
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
                    wDiff: Double) {
  def valueFromFieldName(name: String): Any = {
    val elementName = productElementNames.toList.find(_.toLowerCase == name.toLowerCase).get
    if (elementName == "date") {
      return date.toEpochSecond(ZoneOffset.UTC)
    }

    val index = productElementNames.indexOf(elementName)
    val value = productElement(index)
    if (value == None || value == null) {
      return 0
    }

    value
  }
}

// TODO hash_code vs hashCode
case class ScoresRow(hash_code: Int,
                     score: Double)

object InputRow {
  implicit val decoder: Decoder[InputRow] = deriveDecoder

}
