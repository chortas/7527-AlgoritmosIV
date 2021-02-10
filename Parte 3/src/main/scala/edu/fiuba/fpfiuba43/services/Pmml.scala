package edu.fiuba.fpfiuba43.services

import java.io.File

import cats.Applicative
import cats.implicits._
import edu.fiuba.fpfiuba43.models.InputRow
import org.dmg.pmml.FieldName
import org.jpmml.evaluator.{
  EvaluatorUtil,
  FieldValue,
  LoadingModelEvaluatorBuilder
}

trait Pmml[F[_]] {
  def score(inputRow: InputRow): F[Double]
}

class PmmlImpl[F[_]: Applicative]() extends Pmml[F] {
  private val evaluator = new LoadingModelEvaluatorBuilder()
    .load(new File("model.pmml"))
    .build()

  override def score(inputRow: InputRow): F[Double] = {
    val arguments = new java.util.HashMap[FieldName, FieldValue]()
    evaluator.getActiveFields.forEach(f => {
      arguments.put(
        f.getName,
        f.prepare(inputRow.valueFromFieldName(f.getName.getValue))
      )
    })

    val value = EvaluatorUtil.decodeAll(evaluator.evaluate(arguments))
    value.get("prediction").toString.toDouble.pure[F]
  }
}
