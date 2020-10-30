package fiuba.fp

import cats.effect._

object Run extends App {

    val program = IO { println(s"Hello!") }
    
    program.unsafeRunSync() 
}