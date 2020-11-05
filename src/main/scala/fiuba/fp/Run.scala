package fiuba.fp

import java.nio.file.Paths

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import doobie._
import doobie.implicits._
import fiuba.fp.database.QueryConstructor
import fiuba.fp.models.DataSetRow
import fs2.{Stream, io, text}

import scala.concurrent.ExecutionContext

object Run extends IOApp {

  val converter: Stream[IO, Unit] = Stream.resource(Blocker[IO]).flatMap {
    blocker =>
      io.file
        .readAll[IO](Paths.get("train.csv"), blocker, 4096)
        .through(text.utf8Decode)
        .through(text.lines)
        .drop(1) // remove header
        .dropLastIf(_.isEmpty)
        .map(DataSetRow.toDataSetRowEither(_).map(QueryConstructor.construct))
        .evalMap { // collect errors from both parsing and transacting
          case Right(query) => query.run.transact(transactor).attempt
          case error        => IO.pure(error)
        }
        .map(_.fold(println, _ => ())) // print errors
  }

  implicit val cs = IO.contextShift(ExecutionContext.global)

  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5434/fpalgo",
    "fiuba",
    "password"
  )

  def run(args: List[String]): IO[ExitCode] =
    converter.compile.drain.map(_ => ExitCode.Success)
}
