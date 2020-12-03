package fiuba.fp

import cats.effect.{ContextShift, ExitCode, IO, IOApp}
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux
import fiuba.fp.database.QueryConstructor
import fiuba.fp.ml.{DataSet, Split}
import fiuba.fp.models.DataSetRowSparkSchema

import scala.concurrent.ExecutionContext

object RunTP2 extends IOApp {

  private implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  private val transactor: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5434/fpalgo",
    "fiuba",
    "password"
  )

  private val resultIO: IO[List[DataSetRowSparkSchema]] = QueryConstructor.constructSelect()
    .map(DataSetRowSparkSchema(_))
    .to[List]
    .transact(transactor)


  private def consumeDataSet(dataSet: DataSet[_]): Unit =
    println(s"Train es ${dataSet.train.length}")

  override def run(args: List[String]): IO[ExitCode] = for {
    l <- resultIO
    ds <- Split.splitIO(l)
  } yield {
    consumeDataSet(ds)
    ExitCode.Success
  }
}
