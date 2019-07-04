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

object DisplayUtils {

  implicit class DisplayStringWrapper(val str: String) extends AnyVal {

    /** generate a version of the string that can be displayed */
    def display: String = display(0)

    /** generate a version of the string that can be displayed */
    def display(maxLength: Int = 0): String = {
      // https://www.unicode.org/charts/PDF/U2400.pdf
      val nullChar = '\u2400'
      val backspace = '\u2408'
      val horizontalTab = '\u2409'
      val formFeed = '\u240C'
      val carriageReturn = '\u240D'
      val newline = '\u2424'
      // we handle the carriage return line feed sequence as a special case
      val crlf = "\u240D\u240A"
      // guillemets and ellipsis
      val leftGuillemet = '\u00AB'
      val rightGuillemet = '\u00BB'
      val ellipsis = '\u2026'
      // format string for display
      var formattedString = str
        .replace("\r\n", crlf)
        .replace('\u0000', nullChar)
        .replace('\b', backspace)
        .replace('\t', horizontalTab)
        .replace('\f', formFeed)
        .replace('\r', carriageReturn)
        .replace('\n', newline)
      // if formattedString is too long then truncate and add ellipsis
      if (maxLength > 0 && formattedString.length + 2 > maxLength) {
        require(maxLength > 3, "maxLength is too small")
        formattedString = formattedString.take(maxLength - 3)
        formattedString += ellipsis
      }
      // return formatted string surrounded by guillemets
      s"$leftGuillemet$formattedString$rightGuillemet"
    }

  }

  implicit class DisplayShortWrapper(val n: Short) extends AnyVal {

    def display: String = n.toLong.display

    def display(
      minIntegerDigits: Int = 1,
      maxIntegerDigits: Int = Int.MaxValue
    ): String = {
      n.toLong.display(minIntegerDigits, maxIntegerDigits)
    }

  }

  implicit class DisplayIntWrapper(val n: Int) extends AnyVal {

    def display: String = n.toLong.display

    def display(
      minIntegerDigits: Int = 1,
      maxIntegerDigits: Int = Int.MaxValue
    ): String = {
      n.toLong.display(minIntegerDigits, maxIntegerDigits)
    }

  }

  implicit class DisplayLongWrapper(val n: Long) extends AnyVal {

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

  implicit class DisplayFloatWrapper(val n: Float) extends AnyVal {

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

  implicit class DisplayDoubleWrapper(val n: Double) extends AnyVal {

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
