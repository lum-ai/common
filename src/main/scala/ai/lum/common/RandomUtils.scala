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
import org.apache.commons.lang3.RandomStringUtils

object RandomUtils {

  implicit class RandomWrapper(val random: Random) extends AnyRef {

    def nextBytes(count: Int): Array[Byte] = {
      val result = new Array[Byte](count)
      random.nextBytes(result)
      result
    }

    def nextInt(startInclusive: Int, endExclusive: Int): Int = {
      startInclusive + random.nextInt(endExclusive - startInclusive)
    }

    def nextLong(startInclusive: Long, endExclusive: Long): Long = {
      random.nextDouble(startInclusive, endExclusive).toLong
    }

    def nextDouble(startInclusive: Double, endInclusive: Double): Double = {
      startInclusive + ((endInclusive - startInclusive) * random.nextDouble())
    }

    def nextFloat(startInclusive: Float, endInclusive: Float): Float = {
      startInclusive + ((endInclusive - startInclusive) * random.nextFloat())
    }



    def nextUniform(a: Double, b: Double): Double = {
      random.nextDouble(a, b)
    }

    def nextGaussian(mu: Double, sigma: Double): Double = {
      mu + random.nextGaussian() * sigma
    }

    def nextNormal(mu: Double, sigma: Double): Double = {
      nextGaussian(mu, sigma)
    }

    def nextLogNormal(mu: Double, sigma: Double): Double = {
      Math.exp(nextNormal(mu, sigma))
    }

    def nextExponential(lambda: Double): Double = {
      -Math.log(1 - random.nextDouble()) / lambda
    }

    def nextPareto(alpha: Double): Double = {
      val u = 1 - random.nextDouble()
      1 / Math.pow(u, 1 / alpha)
    }

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



    // TODO implement for LinearSeq (and Stream)
    // maybe reservoir sampling?
    def choice[A](seq: IndexedSeq[A]): A = {
      seq(random.nextInt(seq.length))
    }

    // TODO:
    // - sample(population, k)

  }

}
