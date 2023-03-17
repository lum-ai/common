package ai.lum.common

import org.scalatest._

class TestInterval extends FlatSpec with Matchers {

  behavior of "Interval"

  it should "perform unions in large quantities" in {
    val count = 1000000
    val intervals = 1.to(count).map { length => Interval.ofLength(length - 1, length) }.toList
    val expectedInterval = Interval.open(0, count + count - 1)
    val actualInterval = Interval.union(intervals)

    actualInterval should be (expectedInterval)
  }
}
