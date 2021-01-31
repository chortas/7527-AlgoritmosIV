package fiuba.fp.models

import java.time.LocalDateTime

import fiuba.fp.FpTpSpec
import fiuba.fp.models.DataSetRow._

class DataSetRowTest extends FpTpSpec {

  "An empty field" should "return exception" in {
    toNotNullEither("").isLeft shouldBe true
  }

  "Invalid double fields" should "return exception" in {
    toDoubleNotNullEither("").isLeft shouldBe true
    toDoubleNotNullEither("fasads").isLeft shouldBe true
  }

  "Invalid int fields" should "return exception" in {
    toIntNotNullEither("").isLeft shouldBe true
    toIntNotNullEither("fasads").isLeft shouldBe true
    toIntNotNullEither("34.5").isLeft shouldBe true
    toIntNotNullEither("2147483648").isLeft shouldBe true
  }

  "Invalid varying fields" should "return exception" in {
    toVaryingNotNullEither(1, "").isLeft shouldBe true
    toVaryingNotNullEither(1, "ss").isLeft shouldBe true
  }

  "Invalid local date times" should "return exception" in {
    toLocalDateTimeEither("").isLeft shouldBe true
    toLocalDateTimeEither("05/01/2004").isLeft shouldBe true
    toLocalDateTimeEither("12:00:00 a.m.").isLeft shouldBe true
    toLocalDateTimeEither("05/01/200 12:00:00 a.m.").isLeft shouldBe true
    toLocalDateTimeEither("05/01/2004  12:00:00 a.m.").isLeft shouldBe true
    toLocalDateTimeEither("05/01/2004 12:00:00 a.m").isLeft shouldBe true
    toLocalDateTimeEither("05/01/2004 12:00:00 am").isLeft shouldBe true
    toLocalDateTimeEither("32/01/2004 12:00:00 a.m.").isLeft shouldBe true
    toLocalDateTimeEither("05/13/2004 12:00:00 a.m.").isLeft shouldBe true
    toLocalDateTimeEither("05/01/2004 26:00:00 a.m.").isLeft shouldBe true
    toLocalDateTimeEither("05/01/2004 12:00:90 a.m.").isLeft shouldBe true
  }

  "Invalid lines" should "return exception" in {
    toFieldsEither("").isLeft shouldBe true
    toFieldsEither("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o").isLeft shouldBe true
    toFieldsEither("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q").isLeft shouldBe true
  }

  "A DataSetRow" should "represent a row" in {

    val row = DataSetRow(
      12,
      LocalDateTime.now(),
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

    row.id shouldBe 12
  }

  "A DataSetRow" should "represent the right row" in {

    val row = DataSetRow(
      2923,
      LocalDateTime.of(2015, 11, 5, 0, 0, 0),
      Some(0),
      Some(0),
      Some(0),
      0,
      241.8,
      -2.2,
      "D",
      Some(200300),
      Some(6400),
      Some(0),
      "TONS",
      9.58,
      9.562,
      -241.8
    )

    val line =
      "2923,05/11/2015 12:00:00 a.m.,0,0,0,0,241.8,-2.2,D,200300,6400,0,TONS,9.58,9.562,-241.8"

    DataSetRow.toDataSetRowEither(line) shouldBe Right(row)
  }

  "Bad formatted lines" should "be None" in {
    val badLines = List(
      "715,06/11/2006 12:00:00 a.m.,0,0,0,0,195,0,D,0,0,0,TONS,3.083,NA,-195",
      "964,06/11/2007 12:00:00 a.m.,0,0,0,0,265,0,D,0,0,0,TONS,3.133,NA,-265",
      "988,10/12/2007 12:00:00 a.m.,0,0,0,0,265,0,D,0,0,0,TONS,3.138,NA,-265",
      "5001,10/08/2009 12:00:00 a.m.,0,0,0,0,262,-2,D,100,0,0,TONS,3.829,3+824,-262",
      "1213,06/11/2008 12:00:00 a.m.,0,0,0,0,230,0,D,0,0,0,TONS,3.31,NA,-230",
      "1376,10/07/2009 12:00:00 a.m.,0,0,0,0,257,0,D,500,0,0,TONS,3.808,NA,-257",
      "3003,02/11/2011 12:00:00 a.m.,0,0,0,0,numero,1.f,D,0,0,0,TONS,4.25,4.254,-299.5",
      "1459,06/11/2009 12:00:00 a.m.,0,0,0,0,258,0,D,0,0,0,TONS,3.817,NA,-258",
      "3002,02/11/2011 12:00:00 a.m.,0,0,0,0,numero,1.5,D,0,0,0,TONS,4.25,4.254,-299.5",
      "6001,06/06/2014 12:00:00 a.m.,0,0,0,0,314,-2,D,148000,-4900,0,TONS,8.13,8.135,error",
      "2924,06/11/2015 12:00:00 a.m.,0,0,0,0,241.8,0,D,194900,-5400,0,TONS,9.58,NA,-241.8"
    )

    badLines.foreach(
      line => DataSetRow.toDataSetRowEither(line).isLeft shouldBe true
    )
  }
}
