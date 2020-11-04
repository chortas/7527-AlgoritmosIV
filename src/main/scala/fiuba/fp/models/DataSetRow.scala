package fiuba.fp.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

case class DataSetRow(id: Int,
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
                      wDiff: Double)

object DataSetRow {
  val NUMBER_OF_FIELDS: Int = 16

  def toDataSetRowOption(line: String): Option[DataSetRow] =
    toDataSetRowEither(line).toOption

  def toDataSetRowEither(line: String): Either[Throwable, DataSetRow] = {
    for {
      fields <- toFieldsEither(line)
      id <- fields(0).toIntOption.toRight(new Throwable())
      date <- toLocalDateTimeOption(fields(1)).toRight(new Throwable())
      last <- fields(5).toDoubleOption.toRight(new Throwable())
      close <- fields(6).toDoubleOption.toRight(new Throwable())
      diff <- fields(7).toDoubleOption.toRight(new Throwable())
      dollarBN <- fields(13).toDoubleOption.toRight(new Throwable())
      dollarItau <- fields(14).toDoubleOption.toRight(new Throwable())
      wDiff <- fields(15).toDoubleOption.toRight(new Throwable())
    } yield
      DataSetRow(
        id,
        date,
        fields(2).toDoubleOption,
        fields(3).toDoubleOption,
        fields(4).toDoubleOption,
        last,
        close,
        diff,
        fields(8),
        fields(9).toIntOption,
        fields(10).toIntOption,
        fields(11).toIntOption,
        fields(12),
        dollarBN,
        dollarItau,
        wDiff
      )
  }

  private def toLocalDateTimeOption(field: String): Option[LocalDateTime] = {
    val field2 = field
      .replace("a.m.", "AM")
      .replace("p.m.", "PM")
    Some(
      LocalDateTime
        .parse(field2, DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a"))
    )
  }

  def toFieldsEither(line: String): Either[Throwable, List[String]] = {
    val split = line.split(',')
    split.length match {
      case DataSetRow.NUMBER_OF_FIELDS => Right(split.toList)
      case n =>
        Left(
          new Throwable(
            s"Expected ${DataSetRow.NUMBER_OF_FIELDS} but got ${n} fields"
          )
        )
    }
  }
}
