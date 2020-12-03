package fiuba.fp

import cats.effect.{ContextShift, ExitCode, IO, IOApp}
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux
import fiuba.fp.database.QueryConstructor
import fiuba.fp.ml.{Seed, Split}
import fiuba.fp.models.DataSetRowSparkSchema

import scala.concurrent.ExecutionContext

object RunTP2 extends IOApp {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val transactor: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5434/fpalgo",
    "fiuba",
    "password"
  )

  val query = QueryConstructor.constructSelect()

  val result: doobie.ConnectionIO[List[DataSetRowSparkSchema]] = query
    .map(DataSetRowSparkSchema(_))
    .to[List]

  val otherResult: IO[List[DataSetRowSparkSchema]] = result.transact(transactor)

  def f(dataSet: List[DataSetRowSparkSchema]) =
    Split.split(dataSet)
    .map(dataSet => println(s"Train es ${dataSet.train.length}"))
    .run(Seed(0))
    .value._2

  /*
    .fold(List[DataSetRowSparkSchema]()) { (acc, r) =>
      acc :+ r
    }
    .map(
      Split
        .split(_)
        .map(dataSet => println(s"Train es ${dataSet.train.length}"))
        .run(Seed(0))
    )
   */

  def run(args: List[String]): IO[ExitCode] = for {
    dataSet <- otherResult
  } yield {
    f(dataSet)
    ExitCode.Success
  }
}
