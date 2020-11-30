package fiuba.fp.models

import java.time.ZoneOffset

import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.StructType

case class DataSetRowSparkSchema(id: Int,
                                 date: Long,
                                 open: Option[Double],
                                 high: Option[Double],
                                 low: Option[Double],
                                 last: Double,
                                 close: Double,
                                 diff: Double,
                                 curr: String,
                                 OVol: Option[Int],
                                 Odiff: Option[Int],
                                 OpVol: Option[Int],
                                 unit: String,
                                 dollarBN: Double,
                                 dollarItau: Double,
                                 wDiff: Double)

object DataSetRowSparkSchema {
  def apply(dataSetRow: DataSetRow): DataSetRowSparkSchema = new DataSetRowSparkSchema(
    dataSetRow.id,
    dataSetRow.date.toEpochSecond(ZoneOffset.UTC),
    dataSetRow.open,
    dataSetRow.high,
    dataSetRow.low,
    dataSetRow.last,
    dataSetRow.close,
    dataSetRow.diff,
    dataSetRow.curr,
    dataSetRow.OVol,
    dataSetRow.Odiff,
    dataSetRow.OpVol,
    dataSetRow.unit,
    dataSetRow.dollarBN,
    dataSetRow.dollarItau,
    dataSetRow.wDiff)
}

/**
 * Console app that shows what the schema looks like
 */
private object DataSetRowSparkSchemaRun extends App {
  val schema: StructType = ScalaReflection.schemaFor[DataSetRowSparkSchema].dataType.asInstanceOf[StructType]
  schema.printTreeString
}
