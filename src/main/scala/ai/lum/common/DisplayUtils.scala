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

  implicit class LumAICommonDisplayStringWrapper(val str: String) extends AnyVal {

    /** generate a version of the string that can be displayed */
    def display: String = display(0)

    /** generate a version of the string that can be displayed */
    def display(maxLength: Int = 0): String = {
      // guillemets and ellipsis
      val leftGuillemet = '\u00AB'
      val rightGuillemet = '\u00BB'
      val ellipsis = '\u2026'
      // format string for display
      var formattedString = str
        // https://www.unicode.org/charts/PDF/U2400.pdf
        .replace("\r\n", "\u240D\u240A") // carriage return line feed
        .replace('\u0000', '\u2400') // null
        .replace('\u0001', '\u2401') // start of heading
        .replace('\u0002', '\u2402') // start of text
        .replace('\u0003', '\u2403') // end of text
        .replace('\u0004', '\u2404') // end of transmission
        .replace('\u0005', '\u2405') // enquiry
        .replace('\u0006', '\u2406') // acknowledge
        .replace('\u0007', '\u2407') // bell
        .replace('\u0008', '\u2408') // backspace
        .replace('\u0009', '\u2409') // horizontal tabulation
        .replace('\u000A', '\u2424') // newline (line feed)
        .replace('\u000B', '\u240B') // vertical tabulation
        .replace('\u000C', '\u240C') // form feed
        .replace('\u000D', '\u240D') // carriage return
        .replace('\u000E', '\u240E') // shift out
        .replace('\u000F', '\u240F') // shift in
        .replace('\u0010', '\u2410') // data link escape
        .replace('\u0011', '\u2411') // device control one
        .replace('\u0012', '\u2412') // device control two
        .replace('\u0013', '\u2413') // device control three
        .replace('\u0014', '\u2414') // device control four
        .replace('\u0015', '\u2415') // negative acknowledge
        .replace('\u0016', '\u2416') // synchronous idle
        .replace('\u0017', '\u2417') // end of transmission block
        .replace('\u0018', '\u2418') // cancel
        .replace('\u0019', '\u2419') // end of medium
        .replace('\u001A', '\u241A') // substitute
        .replace('\u001B', '\u241B') // escape
        .replace('\u001C', '\u241C') // file separator
        .replace('\u001D', '\u241D') // group separator
        .replace('\u001E', '\u241E') // record separator
        .replace('\u001F', '\u241F') // unit separator
        .replace('\u007F', '\u2421') // delete
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
