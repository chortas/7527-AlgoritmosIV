package fiuba.fp

import java.io.{FileOutputStream, OutputStream}

import cats.effect.{ContextShift, ExitCode, IO, IOApp}
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux
import fiuba.fp.database.QueryConstructor
import fiuba.fp.ml.{DataSet, Split}
import fiuba.fp.models.DataSetRowSparkSchema
import javax.xml.transform.stream.StreamResult
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.ml.regression.RandomForestRegressor
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{Dataset, SparkSession}
import org.jpmml.model.JAXBUtil
import org.jpmml.model.metro.MetroJAXBUtil
import org.jpmml.sparkml.PMMLBuilder

import scala.concurrent.ExecutionContext

object RunTP2 extends IOApp {

  private implicit val cs: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)

  private val transactor: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5434/fpalgo",
    "fiuba",
    "password"
  )

  private final val spark: SparkSession = SparkSession
    .builder()
    .master("local[*]")
    .getOrCreate()

  val schema: StructType = ScalaReflection
    .schemaFor[DataSetRowSparkSchema]
    .dataType
    .asInstanceOf[StructType]

  private val resultIO: IO[List[DataSetRowSparkSchema]] = QueryConstructor
    .constructSelect()
    .map(DataSetRowSparkSchema(_))
    .to[List]
    .transact(transactor)

  private val dataSetIO: IO[DataSet[DataSetRowSparkSchema]] =
    resultIO.flatMap(Split.splitIO)

  private val sparkDatasetsIO: IO[(Dataset[_], Dataset[_])] =
    dataSetIO.map(dataSet => {
      import spark.implicits._
      val rddTest = spark.sparkContext.makeRDD(dataSet.test)
      val rddTrain = spark.sparkContext.makeRDD(dataSet.train)
      (spark.createDataset(rddTest), spark.createDataset(rddTrain))
    })

  private val consumedDataSet: IO[Unit] = sparkDatasetsIO.map(dataSets => {
    val (dataSetTest, dataSetTrain) = dataSets

    val assembler: VectorAssembler = new VectorAssembler()
      .setInputCols(schema.fieldNames.filter(!_.equals("close")))
      .setOutputCol("features")

    val indexer = new StringIndexer()
      .setInputCol("close")
      .setOutputCol("label")

    val seed = 5043

    val randomForestRegressor = new RandomForestRegressor()
      .setMaxDepth(3)
      .setNumTrees(20)
      .setFeatureSubsetStrategy("auto")
      .setSeed(seed)

    val stages = Array(assembler, indexer, randomForestRegressor)

    val pipeline = new Pipeline().setStages(stages)
    val pipelineModel: PipelineModel = pipeline.fit(dataSetTrain)

    val pipelinePredictionDf = pipelineModel.transform(dataSetTest)
    pipelinePredictionDf.show(10)

    val pmml = new PMMLBuilder(schema, pipelineModel).build

    val os: OutputStream = new FileOutputStream("model.pmml");
    MetroJAXBUtil.marshalPMML(pmml, os);

    spark.close()
  })

  override def run(args: List[String]): IO[ExitCode] =
    consumedDataSet
      .map(_ => ExitCode.Success)
}
