package edu.fiuba.fpfiuba43.services

import cats.Applicative
import cats.implicits._
import edu.fiuba.fpfiuba43.models.ScoresMessage

trait Scores[F[_]] {
  def scores: F[ScoresMessage]
}

class ScoresImpl[F[_]: Applicative]() extends Scores[F] {
  override def scores: F[ScoresMessage] =
    ScoresMessage(42.666).pure[F]
}