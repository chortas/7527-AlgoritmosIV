package fiuba.fp

import java.nio.file.Paths
import java.time.LocalDateTime

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import fiuba.fp.models.DataSetRow
import fs2.{Stream, io, text}

object Run extends IOApp {

  val converter: Stream[IO, Unit] = Stream.resource(Blocker[IO]).flatMap {
    blocker =>
      io.file
        .readAll[IO](Paths.get("train.csv"), blocker, 4096)
        .through(text.utf8Decode)
        .through(text.lines)
        .drop(1) // remove header
        .dropLastIf(_.isEmpty)
        .map(mapLineToRow)
        .map(_.toString)
        .intersperse("\n")
        .through(text.utf8Encode)
        .through(io.file.writeAll(Paths.get("celsius.txt"), blocker))
  }

  def run(args: List[String]): IO[ExitCode] =
    converter.compile.drain.map(_ => ExitCode.Success)

  def mapLineToRow(line: String): Option[DataSetRow] = {
    for {
      fields <- mapLineToFields(line)
      id <- fields(0).toIntOption
      last <- fields(5).toDoubleOption
      close <- fields(6).toDoubleOption
      diff <- fields(7).toDoubleOption
      dollarBN <- fields(13).toDoubleOption
      dollarItau <- fields(14).toDoubleOption
      wDiff <- fields(15).toDoubleOption
    } yield
      DataSetRow(
        id,
        LocalDateTime.MIN,
        None,
        None,
        None,
        last,
        close,
        diff,
        fields(8),
        None,
        None,
        None,
        fields(12),
        dollarBN,
        dollarItau,
        wDiff
      )
  }

  def mapLineToFields(line: String): Option[List[String]] = {
    val split = line.split(',')
    split.length match {
      case DataSetRow.NUMBER_OF_FIELDS => Some(split.toList)
      case _                           => None
    }
  }
}
