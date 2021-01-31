package edu.fiuba.fpfiuba43.services

import cats.effect.Sync
import cats.free.Free
import doobie.{ConnectionIO, Transactor}
import doobie.free.connection
import doobie.implicits._
import edu.fiuba.fpfiuba43.models.{InputRow, ScoresMessage, ScoresRow}

trait Scores[F[_]] {
  def scores(inputRow: InputRow): F[ScoresMessage]
}

class ScoresImpl[F[_]: Sync](transactor: Transactor[F]) extends Scores[F] {

  override def scores(inputRow: InputRow): F[ScoresMessage] =
    scoresQuery(inputRow).transact(transactor)

  private def scoresQuery(inputRow: InputRow): ConnectionIO[ScoresMessage] =
    for {
      scoreOption <- sql"select * from fptp.scores where hash_code = ${inputRow.hashCode}".query[ScoresRow].option
    } yield scoreOption match {
      case Some(sr) => ScoresMessage(sr.hash_code)
      case None => ScoresMessage(99999)
    }
}