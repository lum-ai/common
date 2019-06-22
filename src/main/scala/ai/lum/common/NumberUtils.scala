/*
 * Copyright 2019 lum.ai
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

import java.text.NumberFormat

object NumberUtils {

  implicit class IntWrapper(val n: Int) extends AnyVal {

    def display: String = n.toLong.display

    def display(
      minIntegerDigits: Int = 1,
      maxIntegerDigits: Int = Int.MaxValue
    ): String = {
      n.toLong.display(minIntegerDigits, maxIntegerDigits)
    }

  }

  implicit class FloatWrapper(val n: Float) extends AnyVal {

    def display: String = n.toDouble.display

    def display(
      minIntegerDigits: Int = 1,
      maxIntegerDigits: Int = Int.MaxValue,
      minFractionDigits: Int = 0,
      maxFractionDigits: Int = 2
    ): String = {
      n.toDouble.display(
        minIntegerDigits, maxIntegerDigits,
        minFractionDigits, maxFractionDigits
      )
    }

  }

  implicit class LongWrapper(val n: Long) extends AnyVal {

    def display: String = display(1, Int.MaxValue)

    def display(
      minIntegerDigits: Int = 1,
      maxIntegerDigits: Int = Int.MaxValue
    ): String = {
      val formatter = NumberFormat.getIntegerInstance()
      formatter.setMinimumIntegerDigits(minIntegerDigits)
      if (maxIntegerDigits >= minIntegerDigits) {
        formatter.setMaximumIntegerDigits(maxIntegerDigits)
      }
      formatter.format(n)
    }

  }

  implicit class DoubleWrapper(val n: Double) extends AnyVal {

    def display: String = display(1, Int.MaxValue, 0, 2)

    def display(
      minIntegerDigits: Int = 1,
      maxIntegerDigits: Int = Int.MaxValue,
      minFractionDigits: Int = 0,
      maxFractionDigits: Int = 2
    ): String = {
      val formatter = NumberFormat.getInstance()
      formatter.setMinimumIntegerDigits(minIntegerDigits)
      if (maxIntegerDigits >= minIntegerDigits) {
        formatter.setMaximumIntegerDigits(maxIntegerDigits)
      }
      formatter.setMinimumFractionDigits(minFractionDigits)
      if (maxFractionDigits >= minFractionDigits) {
        formatter.setMaximumFractionDigits(maxFractionDigits)
      }
      formatter.format(n)
    }

  }

}
