package fiuba.fp.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import scala.util.Try

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

  def toDataSetRowEither(line: String): Either[Throwable, DataSetRow] = {
    for {
      fields <- toFieldsEither(line)
      id <- toIntNotNullEither(fields(0))
      date <- toLocalDateTimeEither(fields(1))
      last <- toDoubleNotNullEither(fields(5))
      close <- toDoubleNotNullEither(fields(6))
      diff <- toDoubleNotNullEither(fields(7))
      curr <- toVaryingNotNullEither(1, fields(8))
      unit <- toVaryingNotNullEither(4, fields(12))
      dollarBN <- toDoubleNotNullEither(fields(13))
      dollarItau <- toDoubleNotNullEither(fields(14))
      wDiff <- toDoubleNotNullEither(fields(15))
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
        curr,
        fields(9).toIntOption,
        fields(10).toIntOption,
        fields(11).toIntOption,
        unit,
        dollarBN,
        dollarItau,
        wDiff
      )
  }

  def toNotNullEither(field: String): Either[Throwable, String] = field match {
    case "" => Left(new Throwable("Field should not be null"))
    case f  => Right(f)
  }

  def toIntNotNullEither(field: String): Either[Throwable, Int] =
    toNotNullEither(field).flatMap(
      _.toIntOption.toRight(new Throwable("Field should be an int"))
    )

  def toDoubleNotNullEither(field: String): Either[Throwable, Double] =
    toNotNullEither(field).flatMap(
      _.toDoubleOption.toRight(new Throwable("Field should be a double"))
    )

  def toVaryingNotNullEither(length: Int,
                             field: String): Either[Throwable, String] =
    toNotNullEither(field).filterOrElse(
      _.length <= length,
      new Throwable(s"Value '$field' should be at most $length characters long")
    )

  def toLocalDateTimeEither(field: String): Either[Throwable, LocalDateTime] = {
    val field2 = field
      .replace("a.m.", "AM")
      .replace("p.m.", "PM")
    Try(
      LocalDateTime
        .parse(field2, DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a"))
    ).toEither
  }

  def toFieldsEither(line: String): Either[Throwable, List[String]] = {
    val split = line.split(',')
    split.length match {
      case DataSetRow.NUMBER_OF_FIELDS => Right(split.toList)
      case n =>
        Left(
          new Throwable(
            s"Expected ${DataSetRow.NUMBER_OF_FIELDS} but got $n fields"
          )
        )
    }
  }
}
