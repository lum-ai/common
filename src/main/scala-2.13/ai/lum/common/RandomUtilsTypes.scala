package ai.lum.common

//import scala.collection.BuildFrom
import scala.collection.generic.CanBuildFrom

object RandomUtilsTypes {
  type OnePass[+A] = TraversableOnce[A]
  type BuildableFrom[-From, -A, +C] = CanBuildFrom[From, A, C]
  //  type OnePass[+A] = IterableOnce[A]
  //  trait BuildableFrom[-From, -A, +C] extends BuildFrom[From, A, C]
}
