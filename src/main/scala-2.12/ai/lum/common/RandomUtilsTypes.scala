package ai.lum.common

import scala.collection.generic.CanBuildFrom

object RandomUtilsTypes {
  type OnePass[+A] = TraversableOnce[A]
  type BuildableFrom[-From, -A, +C] = CanBuildFrom[From, A, C]
}
