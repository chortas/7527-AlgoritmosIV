package fiuba.fp.ml

import fiuba.fp.FpTpSpec
import org.scalatest.Assertion

class SplitTest extends FpTpSpec {

  "An empty list" should "be splitted in two empty lists" in assertSplit(
    List(),
    ts => {
      ts.train shouldBe empty
      ts.test shouldBe empty
    }
  )

  "A list with ten ints" should "be splitted in two lists with 7 and 3 elements" in assertSplit(
    List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
    ts => {
      ts.train.length shouldBe 7
      ts.test.length shouldBe 3
    }
  )

  "A list with two ints" should "be splitted in two lists with 1 element each one" in assertSplit(
    List(1, 2),
    ts => {
      ts.train.length shouldBe 1
      ts.test.length shouldBe 1
    }
  )

  "A list with ten strings" should "be splitted in two lists with 7 and 3 elements" in assertSplit(
    List("", "", "", "", "", "", "", "", "", ""),
    ts => {
      ts.train.length shouldBe 7
      ts.test.length shouldBe 3
    }
  )

  def assertSplit(list: List[_],
                  assertions: DataSet[_] => Assertion): Assertion =
    Split
      .split(list)
      .map(assertions(_))
      .runA(Seed(0))
      .value
}
