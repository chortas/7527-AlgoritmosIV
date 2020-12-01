package fiuba.fp.ml

import fiuba.fp.FpTpSpec
import org.scalatest.Assertion

class SplitTest extends FpTpSpec {

  "An empty list" should "be splitted in two empty lists" in assertSplit(
    List(),
    (list1, list2) => {
      list1 shouldBe empty
      list2 shouldBe empty
    }
  )

  def assertSplit(list: List[_],
                  assertions: (List[_], List[_]) => Assertion): Assertion =
    Split
      .split(list)
      .map(lists => assertions(lists))
      .run(Seed(0))
      .value
      ._2
}
