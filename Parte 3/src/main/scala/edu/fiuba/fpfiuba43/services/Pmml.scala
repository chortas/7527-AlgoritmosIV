package edu.fiuba.fpfiuba43.services

import cats.effect.{LiftIO, SyncIO}
import edu.fiuba.fpfiuba43.models.InputRow
import org.dmg.pmml.FieldName
import org.jpmml.evaluator.{EvaluatorUtil, FieldValue, ModelEvaluator}

import scala.jdk.CollectionConverters._

trait Pmml[F[_]] {
  def score(inputRow: InputRow): F[Double]
}

class PmmlImpl[F[_]: LiftIO](evaluator: ModelEvaluator[_])(
  implicit F: LiftIO[F]
) extends Pmml[F] {
  override def score(inputRow: InputRow): F[Double] = {
    val arguments: Map[FieldName, FieldValue] = evaluator.getActiveFields.asScala
      .groupMapReduce(_.getName)(f => f.prepare(inputRow.valueFromFieldName(f.getName.getValue)))((_, curr) => curr)

    SyncIO { evaluator.evaluate(arguments.asJava) }
      .map(EvaluatorUtil.decodeAll(_).get("prediction").toString.toDouble)
      .to(F)
  }
}
