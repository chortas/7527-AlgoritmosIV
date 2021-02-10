package edu.fiuba.fpfiuba43

import cats.effect.{ExitCode, IO, IOApp}
import edu.fiuba.fpfiuba43.http.Fpfiuba43Server
import org.jpmml.evaluator.{LoadingModelEvaluatorBuilder, ModelEvaluator}

import java.io.File


object Main extends IOApp {
  def evaluator(pathname: String): IO[ModelEvaluator[_]] = IO {
    new LoadingModelEvaluatorBuilder().load(new File(pathname)).build()
  }

  def run(args: List[String]): IO[ExitCode] =
    for {
      evaluator <- evaluator(args.iterator.nextOption().getOrElse("model.pmml"))
      server <- new Fpfiuba43Server(evaluator).stream[IO].compile.drain.as(ExitCode.Success)
    } yield server
}
