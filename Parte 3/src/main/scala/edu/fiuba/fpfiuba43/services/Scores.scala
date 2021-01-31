package edu.fiuba.fpfiuba43.services

import cats.Applicative
import cats.implicits._
import edu.fiuba.fpfiuba43.models.{InputRow, ScoresMessage}

trait Scores[F[_]] {
  def scores(inputRow: InputRow): F[ScoresMessage]
}

class ScoresImpl[F[_]: Applicative]() extends Scores[F] {
  override def scores(inputRow: InputRow): F[ScoresMessage] =
    ScoresMessage(inputRow.dollarBN).pure[F]
}