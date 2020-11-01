package fiuba.fp

import java.nio.file.Paths

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
        .map(DataSetRow.toDataSetRowOption)
        .map(_.toString)
        .intersperse("\n")
        .through(text.utf8Encode)
        .through(io.file.writeAll(Paths.get("celsius.txt"), blocker))
  }

  def run(args: List[String]): IO[ExitCode] =
    converter.compile.drain.map(_ => ExitCode.Success)
}
