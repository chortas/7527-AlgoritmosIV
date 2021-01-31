package edu.fiuba.fpfiuba43.services

import cats.Applicative
import cats.implicits._
import edu.fiuba.fpfiuba43.models.{InputRow, ScoresMessage}
import org.dmg.pmml.{FieldName, PMML}
import org.jpmml.evaluator.{Evaluator, EvaluatorUtil, FieldValue, InputField, InputFieldUtil, LoadingModelEvaluatorBuilder, ModelEvaluator, ModelEvaluatorFactory}
import org.jpmml.model.PMMLUtil

import scala.jdk.CollectionConverters._
import java.io.{File, FileInputStream}
import java.util
import org.dmg.pmml.mining.MiningModel
import org.jpmml.evaluator.mining.MiningModelEvaluator

import scala.collection.mutable


trait Scores[F[_]] {
  def scores(inputRow: InputRow): F[ScoresMessage]
}

class ScoresImpl[F[_]: Applicative]() extends Scores[F] {
  override def scores(inputRow: InputRow): F[ScoresMessage] = {
    val pmml = readPMML()
    val evaluator = new MiningModelEvaluator(pmml)
    evaluator.verify()

    val arguments = new java.util.HashMap[FieldName, FieldValue]()
    evaluator.getActiveFields.forEach(
      f => {
        arguments.put(
          f.getName,
          f.prepare(inputRow.valueFromFieldName(f.getName.getValue)))
      }
    )

    val value: util.Map[FieldName, _] = evaluator.evaluate(arguments)
    val value1 = EvaluatorUtil.decodeAll(value)
    ScoresMessage(value1.get("close").toString.toDouble).pure[F]
  }

  // Reads the PMML File and returns a PMML object
  private def readPMML(): PMML = {
    val is = new FileInputStream(new File("model.pmml"))
    try {
      org.jpmml.model.PMMLUtil.unmarshal(is)
    } finally {
      is.close()
    }
  }
}