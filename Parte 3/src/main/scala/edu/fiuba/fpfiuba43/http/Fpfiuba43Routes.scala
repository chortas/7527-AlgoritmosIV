package edu.fiuba.fpfiuba43.http

import cats.effect.Sync
import cats.implicits._
import edu.fiuba.fpfiuba43.models.InputRow
import edu.fiuba.fpfiuba43.services.{HealthCheck, Scores}
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

object Fpfiuba43Routes {

  def healthCheckRoutes[F[_]: Sync](h: HealthCheck[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "health-check" =>
        for {
          healthCheck <- h.healthCheck
          resp <- Ok(healthCheck.asJson)
        } yield resp
    }
  }

  def scoresRoutes[F[_]: Sync](s: Scores[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case req @ POST -> Root / "scores" =>
        for {
          inputRow <- req.decodeJson[InputRow]
          scores <- s.scores(inputRow)
          resp <- Ok(scores.asJson)
        } yield resp
    }
  }
}
