package edu.fiuba.fpfiuba43

import cats.effect.IO
import edu.fiuba.fpfiuba43.http.Fpfiuba43Routes
import edu.fiuba.fpfiuba43.services.HealthCheckImpl
import org.http4s._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult

class HealthCheckSpec extends org.specs2.mutable.Specification {

  "HealthCheck" >> {
    "return 200" >> {
      uriReturns200()
    }
    "return health-check message" >> {
      uriReturnsHealthCheck()
    }
  }

  private[this] val retHealthCheck: Response[IO] = {
    val getHC = Request[IO](Method.GET, uri"/health-check")
    val healthCheck = new HealthCheckImpl[IO]("test")
    Fpfiuba43Routes.healthCheckRoutes(healthCheck).orNotFound(getHC).unsafeRunSync()
  }

  private[this] def uriReturns200(): MatchResult[Status] =
    retHealthCheck.status must beEqualTo(Status.Ok)

  private[this] def uriReturnsHealthCheck(): MatchResult[String] =
    retHealthCheck.as[String].unsafeRunSync() must beEqualTo("""{"version":"0.1","maintainer":"test"}""")
}