package fiuba.fp.models

import java.time.LocalDateTime

case class DataSetRow(
                     id: Int,
                     date: LocalDateTime,
                     open: Option[Double],
                     high: Option[Double],
                     low: Option[Double],
                     last: Double,
                     close: Double,
                     diff: Double,
                     curr: String,
                     OVol: Option[Int],
                     Odiff: Option[Int],
                     OpVol: Option[Int],
                     unit: String,
                     dollarBN: Double,
                     dollarItau: Double,
                     wDiff: Double
                     )