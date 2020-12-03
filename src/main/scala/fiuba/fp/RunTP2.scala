package fiuba.fp

import cats.effect.{ContextShift, ExitCode, IO, IOApp}
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux
import fiuba.fp.database.QueryConstructor
import fiuba.fp.ml.{DataSet, Split}
import fiuba.fp.models.DataSetRowSparkSchema
import org.apache.spark
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.ml.regression.{
  RandomForestRegressionModel,
  RandomForestRegressor
}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.{
  DataFrame,
  Dataset,
  Encoder,
  Encoders,
  SparkSession
}
import org.apache.spark.sql.types.{
  DoubleType,
  IntegerType,
  StructField,
  StructType
}

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

  private val resultIO: IO[List[DataSetRowSparkSchema]] = QueryConstructor
    .constructSelect()
    .map(DataSetRowSparkSchema(_))
    .to[List]
    .transact(transactor)

  private final val spark: SparkSession = SparkSession
    .builder()
    .master("local[*]")
    .getOrCreate()

  val schema: StructType = ScalaReflection
    .schemaFor[DataSetRowSparkSchema]
    .dataType
    .asInstanceOf[StructType]

  import spark.implicits._

  private def consumeDataSet(dataSet: DataSet[DataSetRowSparkSchema]) = {
    val rddTest = spark.sparkContext.makeRDD(dataSet.test)
    val rddTrain = spark.sparkContext.makeRDD(dataSet.train)
    val dataSetTest = spark.createDataset(rddTest)
    val dataSetTrain = spark.createDataset(rddTrain)

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

    spark.close()
  }

  override def run(args: List[String]): IO[ExitCode] =
    for {
      l <- resultIO
      ds <- Split.splitIO(l)
    } yield {
      consumeDataSet(ds)
      ExitCode.Success
    }
}
