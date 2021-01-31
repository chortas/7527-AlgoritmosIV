package edu.fiuba.fpfiuba43.http

import cats.effect.{ConcurrentEffect, Timer}
import cats.implicits._
import edu.fiuba.fpfiuba43.services.{HealthCheckImpl, ScoresImpl}
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext.global

object Fpfiuba43Server {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F]): Stream[F, Nothing] = {

    val healthCheck = new HealthCheckImpl[F]("changeme")
    val scores = new ScoresImpl[F]()
    val httpApp = (
      Fpfiuba43Routes.healthCheckRoutes[F](healthCheck) <+>
        Fpfiuba43Routes.scoresRoutes[F](scores)
    ).orNotFound
    val finalHttpApp = Logger.httpApp(true, true)(httpApp)

    for {
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
