package fiuba.fp.models

import java.time.LocalDateTime

import fiuba.fp.FpTpSpec

class DataSetRowSparkSchemaTest extends FpTpSpec {

  "A DataSetRow" should "be mapped to the right schema" in {

    val row = DataSetRow(
      12,
      LocalDateTime.of(1970, 1, 1, 0, 0),
      None,
      Option(12.3),
      Some(13.6),
      89.9,
      123.45,
      -1213.0,
      "D",
      Some(12),
      None,
      None,
      "TONS",
      123.34,
      567.23,
      1234.5
    )

    val schema = new DataSetRowSparkSchema(12, 0, 0, 12.3, 13.6, 89.9, 123.45,
      -1213.0, 12, 0, 0, 123.34, 567.23, 1234.5)

    DataSetRowSparkSchema.apply(row) shouldBe schema
  }
}
