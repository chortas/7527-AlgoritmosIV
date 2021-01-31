package edu.fiuba.fpfiuba43.services

import cats.Applicative
import cats.implicits._
import edu.fiuba.fpfiuba43.models.HealthCheckMessage

trait HealthCheck[F[_]] {
  def healthCheck: F[HealthCheckMessage]
}

class HealthCheckImpl[F[_]: Applicative](team: String) extends HealthCheck[F] {
  override def healthCheck: F[HealthCheckMessage] =
    HealthCheckMessage("0.1",team).pure[F]
}