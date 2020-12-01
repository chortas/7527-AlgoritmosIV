package fiuba.fp.ml

import fiuba.fp.models.DataSetRow
import cats.data.State

import scala.util.Random

/**
  * Knuth’s 64-bit linear congruential generator from https://typelevel.org/cats/datatypes/state.html
  */
final case class Seed(long: Long) {
  def next = Seed(long * 6364136223846793005L + 1442695040888963407L)
}

object Split {
  def split(list: List[_]): State[Seed, (List[_], List[_])] =
    random().map {
      _.shuffle(list).splitAt((list.length * 0.7).round.toInt)
    }

  /**
    * Random wrapped in a State monad
    */
  private def random(): State[Seed, Random] =
    State(seed => (seed.next, new Random(seed.long)))
}
