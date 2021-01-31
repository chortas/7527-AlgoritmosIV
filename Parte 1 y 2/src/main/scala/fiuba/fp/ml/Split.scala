package fiuba.fp.ml

import cats.data.State
import cats.effect.IO

import scala.util.Random

/**
  * Knuth’s 64-bit linear congruential generator from https://typelevel.org/cats/datatypes/state.html
  */
final case class Seed(long: Long) {
  def next = Seed(long * 6364136223846793005L + 1442695040888963407L)
}

case class DataSet[A](train: List[A], test: List[A])

object Split {
  def splitIO[T](list: List[T]): IO[DataSet[T]] = for {
    initialSeed <- IO(new Random().nextLong())
    l <- IO.eval(Split.split(list).runA(Seed(initialSeed)))
  } yield {
    l
  }

  def split[T](list: List[T]): State[Seed, DataSet[T]] =
    shuffle(list).map { l =>
      val (l1, l2) = l.splitAt((list.length * 0.7).round.toInt)
      DataSet(l1, l2)
    }

  private def shuffle[T](list: List[T]): State[Seed, List[T]] =
    State(seed => (seed.next, new Random(seed.long).shuffle(list)))
}
