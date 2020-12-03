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

  def assertSplit(list: List[_],
                  assertions: DataSet[_] => Assertion): Assertion =
    Split
      .split(list)
      .map(assertions(_))
      .runA(Seed(0))
      .value
}
