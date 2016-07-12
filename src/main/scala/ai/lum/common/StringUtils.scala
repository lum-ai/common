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

import org.apache.commons.lang3.{ StringUtils => ApacheStringUtils }
import org.apache.commons.lang3.StringEscapeUtils
import org.apache.commons.lang3.text.WordUtils

object StringUtils {

  // value classes remove the runtime overhead
  // http://docs.scala-lang.org/overviews/core/value-classes.html#extension-methods
  implicit class StringWrapper(val str: String) extends AnyVal {

    /** Removes diacritics from a string. */
    def stripAccents: String = ApacheStringUtils.stripAccents(str)

    /** Returns a String value for a CSV column enclosed in double quotes, if required. */
    def escapeCsv: String = StringEscapeUtils.escapeCsv(str)

    /** Returns a String value for an unescaped CSV column. */
    def unescapeCsv: String = StringEscapeUtils.unescapeCsv(str)

    /** Escapes the characters in a String using HTML entities. */
    def escapeHtml: String = StringEscapeUtils.escapeHtml4(str)

    /** Unescapes a string containing entity escapes to a string containing the actual Unicode characters corresponding to the escapes. */
    def unescapeHtml: String = StringEscapeUtils.unescapeHtml4(str)

    /** Escapes the characters in a String using Java String rules. */
    def escapeJava: String = StringEscapeUtils.escapeJava(str)

    /** Unescapes any Java literals found in the String. */
    def unescapeJava: String = StringEscapeUtils.unescapeJava(str)

    /** Escapes the characters in a String using Json String rules. */
    def escapeJson: String = StringEscapeUtils.escapeJson(str)

    /** Unescapes any Json literals found in the String. */
    def unescapeJson: String = StringEscapeUtils.unescapeJson(str)

    /** Escapes the characters in a String using XML entities. */
    def escapeXml: String = StringEscapeUtils.escapeXml10(str)

    /** Unescapes a string containing XML entity escapes to a string containing the actual Unicode characters corresponding to the escapes. */
    def unescapeXml: String = StringEscapeUtils.unescapeXml(str)

    /** Splits the provided text on whitespace. */
    def splitOnWhitespace: Array[String] = ApacheStringUtils.splitByWholeSeparatorPreserveAllTokens(str, null)

    /** Removes leading and trailing whitespace and replaces sequences of whitespace characters by a single space. */
    def normalizeSpace: String = ApacheStringUtils.normalizeSpace(str)

    def normalize: String = normalizeSpace.stripAccents.toLowerCase

    def splitCamelCase: Array[String] = ApacheStringUtils.splitByCharacterTypeCamelCase(str)

    def titleCase: String = WordUtils.capitalizeFully(str)

    /** Swaps the case of a String changing upper and title case to lower case, and lower case to upper case. */
    def swapCase: String = ApacheStringUtils.swapCase(str)

    /** Abbreviates a String using ellipses. */
    def abbreviate(maxWidth: Int): String = ApacheStringUtils.abbreviate(str, maxWidth)

    /** Wraps a single line of text, identifying words by ' '. */
    def wordWrap(wrapLength: Int): String = WordUtils.wrap(str, wrapLength)

    /** Find the edit distance to another String */
    def distanceTo(str2: String): Int = ApacheStringUtils.getLevenshteinDistance(str, str2)

  }

}
