package ai.lum.common

/**
  * Itertools for Scala.
  */
package object itertools {

  // cartesian product
  // from https://github.com/clulab/processors/blob/984f0973f4666067ee41c880cc5a1a0577eb909d/odin/src/main/scala/org/clulab/odin/impl/GraphPattern.scala#L91-L97
  // from: List(List(x1, x2, x3), List(y1, y2))
  // to: List(List(x1, y1), List(x1, y2), List(x2, y1), List(x2, y2), List(x3, y1), List(x3, y2))
  def product[A](xss: Seq[Seq[A]]) = xss.foldRight(Seq(Seq[A]())) {
    (xs, lla) => xs.flatMap(x => lla.map(x +: _))
  }

}
