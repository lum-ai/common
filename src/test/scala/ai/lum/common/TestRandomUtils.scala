package ai.lum.common

import RandomUtils.LumAICommonRandomWrapper

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class TestRandomUtils extends Test {
  val seq = Seq(1, 2, 3)

  val array = seq.toArray
  val buffer = seq.toBuffer
  val indexedSeq = seq.toIndexedSeq
  val iterable = seq.toIterable
  val choiceIterator = seq.toIterator
  val sampleIterator = seq.toIterator
  val list = seq.toList
  val set = seq.toSet
  val stream = seq.toStream
  val vector = seq.toVector

  behavior of "RandomUtils"

  it should "choice" in {
    val random = new Random(0)

    random.choice(seq)        should be (1)
    random.choice(array)      should be (2)
    random.choice(buffer)     should be (1)
    random.choice(indexedSeq) should be (3)
    random.choice(iterable)   should be (3)
    random.choice(choiceIterator)   should be (1)
    random.choice(list)       should be (2)
    random.choice(set)        should be (2)
    random.choice(stream)     should be (1)
    random.choice(vector)     should be (2)
  }

  it should "sample" in {
    val random = new Random(0)
    val count = 2

    {
      val actual = random.sample(seq, count)

      actual should contain inOrder (3, 2)
      actual.size should be (count)
      actual.isInstanceOf[Seq[_]] should be (true)
    }
    {
      val actual = random.sample(array, count)

      actual should contain inOrder (1, 3)
      actual.size should be (count)
      actual.isInstanceOf[Array[_]] should be (true)
    }
    {
      val actual = random.sample(buffer, count)

      actual should contain inOrder (1, 3)
      actual.size should be (count)
      actual.isInstanceOf[ArrayBuffer[_]] should be (true)
    }
    {
      val actual = random.sample(indexedSeq, count)

      actual should contain inOrder (1, 2)
      actual.size should be (count)
      actual.isInstanceOf[IndexedSeq[_]] should be (true)
    }
    {
      val actual = random.sample(iterable, count)

      actual should contain allOf (1, 2) // Order is not guaranteed.
      actual.size should be (count)
      actual.isInstanceOf[Iterable[_]] should be (true)
    }
    {
      val actual = random.sample(sampleIterator, count)

      actual.toStream should contain inOrder (1, 2)
      actual.size should be (0) // We have already iterated.
      actual.isInstanceOf[Iterator[_]] should be (true)
    }
    {
      val actual = random.sample(list, count)

      actual should contain inOrder (1, 2)
      actual.size should be (count)
      actual.isInstanceOf[List[_]] should be (true)
    }
    {
      val actual = random.sample(set, count)

      actual should contain allOf (3, 2) // Sets are unordered.
      actual.size should be (count)
      actual.isInstanceOf[Set[_]] should be (true)
    }
    {
      val actual = random.sample(stream, count)

      actual should contain inOrder (3, 2)
      actual.size should be (count)
      actual.isInstanceOf[Stream[_]] should be (true)
    }
    {
      val actual = random.sample(vector, count)

      actual should contain inOrder (1, 2)
      actual.size should be (count)
      actual.isInstanceOf[Vector[_]] should be (true)
    }
  }
}
