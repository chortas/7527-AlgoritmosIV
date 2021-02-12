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

class ScoresImpl[F[_]: Async](pmml: Pmml[F], transactor: Transactor[F])(
  implicit contextShift: ContextShift[F]
) extends Scores[F] {

  override def scores(inputRow: InputRow): F[ScoresMessage] = {
    transactor.resource.use { transactor =>
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
