/*
 * Copyright 2016 lum.ai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.lum.common

import scala.util.Random
import scala.language.higherKinds
import scala.collection.mutable.ArrayBuffer
import scala.collection.generic.CanBuildFrom
import org.apache.commons.lang3.RandomStringUtils

object RandomUtils {

  implicit class RandomWrapper(val random: Random) extends AnyVal {

    /** Creates an array of random bytes. */
    def nextBytes(count: Int): Array[Byte] = {
      val result = new Array[Byte](count)
      random.nextBytes(result)
      result
    }

    /** Returns a random integer within the specified range. */
    def nextInt(startInclusive: Int, endExclusive: Int): Int = {
      startInclusive + random.nextInt(endExclusive - startInclusive)
    }

    /** Returns a random long within the specified range. */
    def nextLong(startInclusive: Long, endExclusive: Long): Long = {
      random.nextDouble(startInclusive, endExclusive).toLong
    }

    /** Returns a random double within the specified range. */
    def nextDouble(startInclusive: Double, endInclusive: Double): Double = {
      startInclusive + ((endInclusive - startInclusive) * random.nextDouble())
    }

    /** Returns a random float within the specified range. */
    def nextFloat(startInclusive: Float, endInclusive: Float): Float = {
      startInclusive + ((endInclusive - startInclusive) * random.nextFloat())
    }

    /**
     * Gaussian distribution. mu is the mean, and sigma is the
     * standard deviation.
     */
    def nextGaussian(mu: Double, sigma: Double): Double = {
      mu + random.nextGaussian() * sigma
    }

    /**
     * Log normal distribution. If you take the natural logarithm of this
     * distribution, you’ll get a normal distribution with mean mu and
     * standard deviation sigma. mu can have any value, and sigma must be
     * greater than zero.
     */
    def nextLogNormal(mu: Double, sigma: Double): Double = {
      Math.exp(nextGaussian(mu, sigma))
    }

    /**
     * Exponential distribution. lambda is 1.0 divided by the desired mean.
     * It should be nonzero.  Returned values range from 0 to positive
     * infinity if lambda is positive, and from negative infinity to 0
     * if lambda is negative.
     */
    def nextExponential(lambda: Double): Double = {
      -Math.log(1 - random.nextDouble()) / lambda
    }

    /** Pareto distribution. alpha is the shape parameter. */
    def nextPareto(alpha: Double): Double = {
      val u = 1 - random.nextDouble()
      1 / Math.pow(u, 1 / alpha)
    }

    /**
     * Weibull distribution.
     * alpha is the scale parameter and beta is the shape parameter.
     */
    def nextWeibull(alpha: Double, beta: Double): Double = {
      val u = 1 - random.nextDouble()
      alpha * Math.pow(-Math.log(u), 1 / beta)
    }



    def randomString(count: Int): String = {
      RandomStringUtils.random(count, 0, 0, false, false, null, random.self)
    }

    def randomString(count: Int, chars: Array[Char]): String = {
      if (chars == null) {
        randomString(count)
      } else {
        RandomStringUtils.random(count, 0, chars.length, false, false, chars, random.self)
      }
    }

    def randomString(count: Int, chars: String): String = {
      if (chars == null) {
        randomString(count)
      } else {
        randomString(count, chars.toCharArray())
      }
    }

    def randomAlphanumeric(count: Int): String = {
      RandomStringUtils.random(count, 0, 0, true, true, null, random.self)
    }

    def randomAlphabetic(count: Int): String = {
      RandomStringUtils.random(count, 0, 0, true, false, null, random.self)
    }

    def randomNumeric(count: Int): String = {
      RandomStringUtils.random(count, 0, 0, false, true, null, random.self)
    }

    def randomAscii(count: Int): String = {
      RandomStringUtils.random(count, 32, 127, false, false, null, random.self)
    }

    def choice[A](xs: TraversableOnce[A]): A = {
      require(xs.nonEmpty, "collection is empty")
      xs match {
        case indexed: IndexedSeq[A] => indexed(random.nextInt(indexed.size))
        case _ => sampleWithoutReplacement(xs, 1).toIterator.next
      }
    }

    def sampleWithReplacement[A, CC[X] <: TraversableOnce[X]](xs: CC[A], k: Int)(implicit cbf: CanBuildFrom[CC[A], A, CC[A]]): CC[A] = {
      require(xs.nonEmpty, "population is empty")
      require(k >= 0, "sample size must be non-negative")
      xs match {
        case indexed: IndexedSeq[A] => // if traversable is indexed then generate k random indices
          val n = indexed.size
          val builder = cbf(xs)
          for (i <- 0 until k) {
            builder += indexed(random.nextInt(n))
          }
          builder.result()
        case _ => // reservoir sampling with replacement
          val iter = xs.toIterator
          var x = iter.next // we know the traversable is not empty
          val buffer = ArrayBuffer.fill(k)(x)
          var i = 2d
          while (iter.hasNext) {
            x = iter.next
            for (j <- 0 until k) {
              val r = random.nextDouble()
              if (r <= 1 / i) buffer(j) = x
            }
            i += 1
          }
          (cbf(xs) ++= buffer).result()
      }
    }

    // reservoir sampling
    def sampleWithoutReplacement[A, CC[X] <: TraversableOnce[X]](xs: CC[A], k: Int)(implicit cbf: CanBuildFrom[CC[A], A, CC[A]]): CC[A] = {
      require(xs.nonEmpty, "population is empty")
      require(k >= 0, "sample size must be non-negative")
      val buffer = new ArrayBuffer[A](k)
      val iter = xs.toIterator
      for (i <- 0 until k) {
        if (!iter.hasNext) sys.error("sample size larger than population")
        buffer += iter.next
      }
      var i = k
      while (iter.hasNext) {
        val x = iter.next
        val j = random.nextInt(i)
        if (j < k) buffer(j) = x
        i += 1
      }
      (cbf(xs) ++= buffer).result()
    }

  }

}
