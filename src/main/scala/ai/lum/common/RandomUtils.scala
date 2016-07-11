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



    def randomBinary(count: Int): String = {
      randomString(count, Array('0', '1'))
    }

    def randomOctal(count: Int): String = {
      randomString(count, Array('0', '1', '2', '3', '4', '5', '6', '7'))
    }

    def randomHexadecimal(count: Int): String = {
      randomString(count, Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'))
    }


    // TODO:
    // - choice(seq)
    // - sample(population, k)
    // - useful distributions

  }

}
