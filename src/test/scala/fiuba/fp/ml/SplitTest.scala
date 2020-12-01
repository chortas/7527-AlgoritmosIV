package fiuba.fp.ml

import fiuba.fp.FpTpSpec

class SplitTest extends FpTpSpec {

  "An empty list" should "be splitted in two empty lists" in (for {
    list <- Split.split(List())
  } yield {
    list._1 shouldBe empty
    list._2 shouldBe empty
  }).run(Seed(0)).value._2
}
