package fiuba.fp

import java.nio.file.Paths

import cats.effect.{Blocker, ContextShift, ExitCode, IO, IOApp}
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux
import fiuba.fp.database.QueryConstructor
import fiuba.fp.models.DataSetRow
import fs2.{Stream, io, text}

import scala.concurrent.ExecutionContext

object RunTP1 extends IOApp {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val converter: Stream[IO, Either[Throwable, Int]] = for {
    blocker <- Stream.resource(Blocker[IO])
    results <- io.file
      .readAll[IO](Paths.get("train.csv"), blocker, 4096)
      .through(text.utf8Decode)
      .through(text.lines)
      .drop(1) // remove header
      .dropLastIf(_.isEmpty)
      .map(
        DataSetRow.toDataSetRowEither(_).map(QueryConstructor.constructInsert)
      )
      .evalMap { // collect errors from both parsing and transacting
        case Right(query) => query.run.transact(transactor).attempt
        case Left(error)  => IO.pure[Either[Throwable, Int]](Left(error))
      }
  } yield results

  val transactor: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5434/fpalgo",
    "fiuba",
    "password"
  )

  def run(args: List[String]): IO[ExitCode] =
    converter.compile
      .fold(0) { (acc, r) =>
        acc + r.fold(error => {
          println(error)
          0
        }, i => i)
      }
      .map(count => println(s"The number of rows written is $count"))
      .map(_ => ExitCode.Success)
}
