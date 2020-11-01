package fiuba.fp

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import fs2.{Stream, io, text}
import java.nio.file.Paths
import java.time.LocalDateTime

import fiuba.fp.models.DataSetRow

object Run extends IOApp {

    val converter: Stream[IO, Unit] = Stream.resource(Blocker[IO]).flatMap { blocker =>
        io.file.readAll[IO](Paths.get("train.csv"), blocker, 4096)
          .through(text.utf8Decode)
          .through(text.lines)
          .drop(1) // remove header
          .map(mapLineToRow)
          .map(_.toString)
          .intersperse("\n")
          .through(text.utf8Encode)
          .through(io.file.writeAll(Paths.get("celsius.txt"), blocker))
    }

    def run(args: List[String]): IO[ExitCode] =
        converter.compile.drain.map(_ => ExitCode.Success)

    def mapLineToRow(line: String): Option[DataSetRow] = Some(DataSetRow(
        1, LocalDateTime.MIN, None, None, None, 0, 0, 0, "", None, None, None, "", 0, 0, 0))
}