package edu.fiuba.fpfiuba43.services

import cats.effect._
import cats.implicits._
import edu.fiuba.fpfiuba43.models.{InputRow, ScoresMessage}

trait Scores[F[_]] {
  def scores(inputRow: InputRow): F[ScoresMessage]
}

class ScoresImpl[F[_]: Async](pmml: Pmml[F], repository: Repository[F])
    extends Scores[F] {

  override def scores(inputRow: InputRow): F[ScoresMessage] =
    for {
      scoreOption <- repository.findScore(inputRow)
      scoreValue <- scoreOption match {
        case Some(value) => value.score.pure[F]
        case None =>
          pmml.score(inputRow).flatMap { score =>
            repository
              .storeScore(inputRow, score)
              .map(_ => score)
          }
      }
    } yield ScoresMessage(scoreValue)
}
