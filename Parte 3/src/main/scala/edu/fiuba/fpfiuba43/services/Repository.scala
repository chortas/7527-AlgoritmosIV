package edu.fiuba.fpfiuba43.services

import cats.effect._
import doobie.implicits._
import edu.fiuba.fpfiuba43.models.{InputRow, ScoresRow}

trait Repository[F[_]] {
  def findScore(inputRow: InputRow): F[Option[ScoresRow]]
  def storeScore(inputRow: InputRow, score: Double): F[Int]
}

class RepositoryImpl[F[_]: Async](resource: Resource[F, doobie.Transactor[F]])(
  implicit contextShift: ContextShift[F]
) extends Repository[F] {

  def findScore(inputRow: InputRow): F[Option[ScoresRow]] =
    resource.use { transactor =>
      sql"select * from fptp.scores where hash_code = ${inputRow.hashCode}"
        .query[ScoresRow]
        .option
        .transact(transactor)
    }

  def storeScore(inputRow: InputRow, score: Double): F[Int] =
    resource.use { transactor =>
      sql"insert into fptp.scores (hash_code, score) values (${inputRow.hashCode}, $score)".update.run
        .transact(transactor)
    }
}
