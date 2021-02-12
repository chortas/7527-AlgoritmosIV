package edu.fiuba.fpfiuba43

import cats.effect.{ContextShift, IO}
import cats.implicits._
import edu.fiuba.fpfiuba43.http.Fpfiuba43Routes
import edu.fiuba.fpfiuba43.models.{InputRow, ScoresRow}
import edu.fiuba.fpfiuba43.services.{
  Pmml,
  Repository,
  ScoresImpl,
}
import io.circe._
import io.circe.literal._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult

import scala.concurrent.ExecutionContext

class ScoresSpec extends org.specs2.mutable.Specification {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  val inputRow =
    json"""{
            "id": 158,
            "date": "2020-12-02T14:49:15.841609",
            "last": 0.0,
            "close": 148.0,
            "diff": 0.0,
            "curr": "D",
            "unit": "TONS",
            "dollarBN": 2.919,
            "dollarItau": 2.91,
            "wDiff": -148.0
          }"""

  "Scores" >> {
    "return score from pmml when not in repository" >> {
        val expectedScore = 2.3
        val retScores = createRetScores(inputRow, expectedScore.pure[IO], None.pure[IO], 0.pure[IO])
        retScores.status must beEqualTo(Status.Ok)
        retScores.as[String].unsafeRunSync() must beEqualTo(
          s"""{"score":$expectedScore}"""
        )
    }
    "returns score from db and avoids calculation" >> {
      val expectedScore = 2.3
      val retScores = createRetScores(inputRow, IO.never, Some(ScoresRow(inputRow.hashCode(), expectedScore)).pure[IO], 0.pure[IO])
      retScores.status must beEqualTo(Status.Ok)
      retScores.as[String].unsafeRunSync() must beEqualTo(
        s"""{"score":$expectedScore}"""
      )
    }
  }

  private[this] def createRetScores(body: Json, pmmlScoreIO: IO[Double], findScoreIO: IO[Option[ScoresRow]], storeScoreIO: IO[Int]) = {
    val getScore = Request[IO](Method.POST, uri"/scores").withEntity(body)
    val pmml = new Pmml[IO] {
      override def score(inputRow: InputRow): IO[Double] = pmmlScoreIO
    }
    val repository = new Repository[IO] {
      override def findScore(inputRow: InputRow): IO[Option[ScoresRow]] = findScoreIO
      override def storeScore(inputRow: InputRow, score: Double): IO[Int] = storeScoreIO
    }
    val scores = new ScoresImpl[IO](pmml, repository)
    Fpfiuba43Routes
      .scoresRoutes(scores)
      .orNotFound(getScore)
      .unsafeRunSync()
  }
}
