package edu.fiuba.fpfiuba43

import cats.effect.{ExitCode, IO, IOApp}
import edu.fiuba.fpfiuba43.http.Fpfiuba43Server

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    Fpfiuba43Server.stream[IO].compile.drain.as(ExitCode.Success)
}
