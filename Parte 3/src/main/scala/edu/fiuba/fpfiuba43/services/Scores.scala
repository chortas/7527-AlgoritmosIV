package edu.fiuba.fpfiuba43.services

import cats.effect._
import cats.implicits._
import doobie.ExecutionContexts
import doobie.hikari._
import doobie.implicits._
import edu.fiuba.fpfiuba43.models.{InputRow, ScoresMessage, ScoresRow}

trait Scores[F[_]] {
  def scores(inputRow: InputRow): F[ScoresMessage]
}

class ScoresImpl[F[_]: Async](pmml: Pmml[F])(
  implicit contextShift: ContextShift[F]
) extends Scores[F] {

  val transactorResource: Resource[F, HikariTransactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](32) // our connect EC
      be <- Blocker[F] // our blocking EC
      xa <- HikariTransactor.newHikariTransactor[F](
        "org.postgresql.Driver",
        "jdbc:postgresql://localhost:5434/fpalgo",
        "fiuba",
        "password",
        ce, // await connection here
        be // execute JDBC operations here
      )
    } yield xa

  override def scores(inputRow: InputRow): F[ScoresMessage] = {
    transactorResource.use { transactor =>
      for {
        scoreOption <- sql"select * from fptp.scores where hash_code = ${inputRow.hashCode}"
          .query[ScoresRow]
          .option
          .transact(transactor)
        score <- scoreOption match {
          case Some(value) => value.score.pure[F]
          case None =>
            pmml.score(inputRow).flatMap { score =>
              sql"insert into fptp.scores (hash_code, score) values (${inputRow.hashCode}, $score)".update.run
                .transact(transactor)
                .map(_ => score)
            }
        }
      } yield ScoresMessage(score)
    }
  }
}
