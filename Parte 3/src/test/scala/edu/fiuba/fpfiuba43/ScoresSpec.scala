package edu.fiuba.fpfiuba43

import cats.effect.{ContextShift, IO}
import cats.implicits._
import edu.fiuba.fpfiuba43.http.Fpfiuba43Routes
import edu.fiuba.fpfiuba43.models.InputRow
import edu.fiuba.fpfiuba43.services.{Pmml, ScoresImpl, TransactorImpl}
import io.circe._
import io.circe.literal._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult

import scala.concurrent.ExecutionContext

class ScoresSpec extends org.specs2.mutable.Specification {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  "Scores" >> {
    "return right score" >> {
      uriReturnsRightScore()
    }
  }

  private[this] def createRetScores(body: Json, scoreIO: IO[Double]) = {
    val getScore = Request[IO](Method.POST, uri"/scores").withEntity(body)
    val transactor = new TransactorImpl[IO]()
    val pmml = new Pmml[IO] {
      override def score(inputRow: InputRow): IO[Double] = scoreIO
    }
    val scores = new ScoresImpl[IO](pmml, transactor)
    Fpfiuba43Routes
      .scoresRoutes(scores)
      .orNotFound(getScore)
      .unsafeRunSync()
  }

  private[this] def uriReturnsRightScore(): MatchResult[String] = {
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
    val expectedScore = 2.3
    val retScores = createRetScores(inputRow, expectedScore.pure[IO])
    retScores.status must beEqualTo(Status.Ok)
    retScores.as[String].unsafeRunSync() must beEqualTo(
      s"""{"score":$expectedScore}"""
    )
  }

  private[this] def knownHashCodeReturnsCachedValue(): MatchResult[String] = {
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
    val expectedScore = 2.3
    val retScores = createRetScores(inputRow, expectedScore.pure[IO])
    retScores.status must beEqualTo(Status.Ok)
    retScores.as[String].unsafeRunSync() must beEqualTo(
      s"""{"score":$expectedScore}"""
    )
  }
}
