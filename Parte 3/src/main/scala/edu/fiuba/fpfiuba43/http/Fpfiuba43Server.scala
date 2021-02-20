package edu.fiuba.fpfiuba43.http

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cats.implicits._
import edu.fiuba.fpfiuba43.services._
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import org.jpmml.evaluator.ModelEvaluator

import scala.concurrent.ExecutionContext.global

class Fpfiuba43Server(evaluator: ModelEvaluator[_]) {
  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F],
                                     cs: ContextShift[F],
  ): Stream[F, Nothing] = {

    val healthCheck = new HealthCheckImpl[F]("195009")
    val pmml = new PmmlImpl[F](evaluator)
    val transactor = new TransactorImpl[F]()
    val repository = new RepositoryImpl[F](transactor.resource)
    val scores = new ScoresImpl[F](pmml, repository)
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
