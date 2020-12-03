package fiuba.fp.models

import java.time.ZoneOffset

import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.StructType

case class DataSetRowSparkSchema(id: Int,
                                 date: Long,
                                 open: Double,
                                 high: Double,
                                 low: Double,
                                 last: Double,
                                 close: Double,
                                 diff: Double,
                                 OVol: Int,
                                 Odiff: Int,
                                 OpVol: Int,
                                 dollarBN: Double,
                                 dollarItau: Double,
                                 wDiff: Double)

object DataSetRowSparkSchema {
  def apply(dataSetRow: DataSetRow): DataSetRowSparkSchema =
    new DataSetRowSparkSchema(
      dataSetRow.id,
      dataSetRow.date.toEpochSecond(ZoneOffset.UTC),
      dataSetRow.open.getOrElse(0),
      dataSetRow.high.getOrElse(0),
      dataSetRow.low.getOrElse(0),
      dataSetRow.last,
      dataSetRow.close,
      dataSetRow.diff,
      dataSetRow.OVol.getOrElse(0),
      dataSetRow.Odiff.getOrElse(0),
      dataSetRow.OpVol.getOrElse(0),
      dataSetRow.dollarBN,
      dataSetRow.dollarItau,
      dataSetRow.wDiff
    )
}

/**
  * Console app that shows what the schema looks like
  */
private object DataSetRowSparkSchemaRun extends App {
  val schema: StructType = ScalaReflection
    .schemaFor[DataSetRowSparkSchema]
    .dataType
    .asInstanceOf[StructType]
  schema.printTreeString
}
