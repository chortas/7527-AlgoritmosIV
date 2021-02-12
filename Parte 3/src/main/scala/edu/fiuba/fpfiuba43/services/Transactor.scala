package edu.fiuba.fpfiuba43.services

import cats.effect._
import doobie.ExecutionContexts
import doobie.hikari._

trait Transactor[F[_]] {
  val resource: Resource[F, HikariTransactor[F]]
}

class TransactorImpl[F[_]: Async](implicit contextShift: ContextShift[F])
    extends Transactor[F] {

  val resource: Resource[F, HikariTransactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](32) // our connect EC
      be <- Blocker[F] // our blocking EC
      xa <- HikariTransactor.newHikariTransactor[F](
        "org.postgresql.Driver",
        "jdbc:postgresql://localhost:5434/fpalgo",
        "fiuba",
        "password",
        ce, // await connection here
        be // execute JDBC operations here
      )
    } yield xa
}
