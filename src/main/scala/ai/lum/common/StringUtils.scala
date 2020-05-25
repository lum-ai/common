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

import scala.collection.JavaConverters._
import org.apache.commons.lang3.{ StringUtils => ApacheStringUtils }
import org.apache.commons.text.WordUtils
import org.apache.commons.text.StringEscapeUtils
import org.apache.commons.text.StringSubstitutor
import com.ibm.icu.text.Normalizer2

object StringUtils {

  // value classes remove the runtime overhead
  // http://docs.scala-lang.org/overviews/core/value-classes.html#extension-methods
  implicit class LumAICommonStringWrapper(val str: String) extends AnyVal {

    /** Returns a string with the java string literal that would produce the original string.
     *  Similar to python's `repr(string)`.
     */
    def toJavaLiteral: String = s""""${str.escapeJava}""""

    /** Removes diacritics from a string. */
    def stripAccents: String = ApacheStringUtils.stripAccents(str)

    /** Returns a literal pattern String for the specified String.
     *  This method differs from scala.util.matching.Regex.quote()
     *  in that it adds backslashes to regex metacharacters instead
     *  of surrounding the string with \Q and \E
     */
    def escapeRegex: String = {
      val metacharacters = "<([{\\^-=$!|]})?*+.>"
      str.map(c => if (metacharacters contains c) s"\\$c" else c).mkString
    }

    /**
     * Returns a String value for a CSV column enclosed in double quotes,
     * if required.
     */
    def escapeCsv: String = StringEscapeUtils.escapeCsv(str)

    /** Returns a String value for an unescaped CSV column. */
    def unescapeCsv: String = StringEscapeUtils.unescapeCsv(str)

    /** Escapes the characters in a String using HTML entities. */
    def escapeHtml: String = StringEscapeUtils.escapeHtml4(str)

    /**
     * Unescapes a string containing entity escapes to a string containing
     * the actual Unicode characters corresponding to the escapes.
     */
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

    /**
     * Unescapes a string containing XML entity escapes to a string
     * containing the actual Unicode characters corresponding to the
     * escapes.
     */
    def unescapeXml: String = StringEscapeUtils.unescapeXml(str)

    /** Splits the provided text on whitespace. */
    def splitOnWhitespace: Array[String] = ApacheStringUtils.split(str)

    /**
     * Removes leading and trailing whitespace and replaces sequences of
     * whitespace characters by a single space.
     */
    def normalizeSpace: String = ApacheStringUtils.normalizeSpace(str)

    def splitCamelCase: Array[String] = ApacheStringUtils.splitByCharacterTypeCamelCase(str)

    /** Converts all the whitespace separated words in the string into
     *  capitalized words, that is each word is made up of a titlecase
     *  character and then a series of lowercase characters.
     */
    def capitalizeFully: String = WordUtils.capitalizeFully(str)

    /**
     * Swaps the case of a String changing upper and title case to
     * lower case, and lower case to upper case.
     */
    def swapCase: String = ApacheStringUtils.swapCase(str)

    /** Extracts the initial characters from each word in the String. */
    def initials: String = WordUtils.initials(str)

    /** Wraps a single line of text, identifying words by ' '. */
    def wordWrap(wrapLength: Int): String = WordUtils.wrap(str, wrapLength)

    /** Replaces all the occurrences of variables with their matching values
     *  from the map.
     */
    def replaceVariables(values: Map[String, String]): String = {
      val sub = new StringSubstitutor(values.asJava)
      sub.setEnableSubstitutionInVariables(true)
      sub.setEnableUndefinedVariableException(true)
      sub.replace(str)
    }

    /** Checks if the string contains only lowercase characters. */
    def isAllLowerCase: Boolean = ApacheStringUtils.isAllLowerCase(str)

    /** Checks if the string contains only uppercase characters. */
    def isAllUpperCase: Boolean = ApacheStringUtils.isAllUpperCase(str)

    /** Checks if string contains only whitespace.
     *  Note that we consider the empty string to be whitespace.
     */
    def isWhitespace: Boolean = ApacheStringUtils.isWhitespace(str)

    /** Checks if the string contains only Unicode letters. */
    def isAlphabetic: Boolean = ApacheStringUtils.isAlpha(str)

    /** Checks if the string contains only Unicode letters or digits. */
    def isAlphanumeric: Boolean = ApacheStringUtils.isAlphanumeric(str)

    /** Checks if the string contains only Unicode digits. */
    def isNumeric: Boolean = ApacheStringUtils.isNumeric(str)

    /** Checks if string contains only ASCII characters.
     *  Note that we consider the empty string to be ascii.
     */
    def isAscii: Boolean = """^\p{ASCII}*$""".r.findFirstIn(str).isDefined

    /** Checks if the string contains only ASCII printable characters. */
    def isAsciiPrintable: Boolean = ApacheStringUtils.isAsciiPrintable(str)

    /** Checks if string contains only ASCII punctuation characters.
     *  One of !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
     */
    def isPunctuation: Boolean = """^\p{Punct}+$""".r.findFirstIn(str).isDefined

    /** Centers a String in a larger String of the specified size using the space character (' '). */
    def center(size: Int): String = ApacheStringUtils.center(str, size)

    /** Centers a String in a larger String of the specified size.
     *  Uses a supplied character as the value to pad the String with.
     */
    def center(size: Int, padChar: Char): String = ApacheStringUtils.center(str, size, padChar)

    /** Centers a String in a larger String of the specified size.
     *  Uses a supplied String as the value to pad the String with.
     */
    def center(size: Int, padStr: String): String = ApacheStringUtils.center(str, size, padStr)

    /** Left pad a String with spaces (' '). The String is padded to the specified size. */
    def leftPad(size: Int): String = ApacheStringUtils.leftPad(str, size)

    /** Left pad a String with a specified character. The String is padded to the specified size. */
    def leftPad(size: Int, padChar: Char): String = ApacheStringUtils.leftPad(str, size, padChar)

    /** Left pad a String with a specified String. The String is padded to the specified size. */
    def leftPad(size: Int, padStr: String): String = ApacheStringUtils.leftPad(str, size, padStr)

    /** Right pad a String with spaces (' '). The String is padded to the specified size. */
    def rightPad(size: Int): String = ApacheStringUtils.rightPad(str, size)

    /** Right pad a String with a specified character. The String is padded to the specified size. */
    def rightPad(size: Int, padChar: Char): String = ApacheStringUtils.rightPad(str, size, padChar)

    /** Right pad a String with a specified String. The String is padded to the specified size. */
    def rightPad(size: Int, padStr: String): String = ApacheStringUtils.rightPad(str, size, padStr)

    /** Canonical unicode representation.
     *  Uses NFC as recommended by the W3C in https://www.w3.org/TR/charmod-norm/
     */
    def canonicalUnicode: String = Normalizer2.getNFCInstance().normalize(str)

    /** Unicode normalization */
    def normalizeUnicode: String = normalizeUnicode(false, false, Map.empty, Map.empty)

    /** Unicode normalization.
     *  NFKCCasefold, remove diacritics, replace some chars to ascii versions, collapse spaces.
     */
    def normalizeUnicodeAggressively: String = normalizeUnicode(true, true, LumAICommonStringWrapper.preMapping, LumAICommonStringWrapper.postMapping)

    /** Unicode normalization */
    def normalizeUnicode(casefold: Boolean, removeDiacritics: Boolean, preMapping: Map[String, String], postMapping: Map[String, String]): String = {
      var result = str
      // replace chars pre normalization
      for ((k,v) <- preMapping) {
        result = result.replaceAllLiterally(k, v)
      }
      // normalize
      val normalizer = if (casefold) Normalizer2.getNFKCCasefoldInstance() else Normalizer2.getNFKCInstance()
      result = normalizer.normalize(result)
      // replace chars post normalization
      for ((k,v) <- postMapping) {
        result = result.replaceAllLiterally(k, normalizer.normalize(v))
      }
      // remove diacritics
      if (removeDiacritics) {
        result = result.stripAccents
        // stripAccents converts to NFD, so convert back to NFKC
        // this may be unnecessary but better safe than sorry
        result = normalizer.normalize(result)
      }
      // return result
      result
    }

  }

  object LumAICommonStringWrapper {

    // There are some characters that we want to normalize
    // before the NFKC normalization has taken place.
    // For example, the string catchphrase™ would become
    // catchphraseTM after the NFKC normalization.
    // Instead we want to convert it to catchphrase(TM).
    val preMapping: Map[String, String] = Map(
      "\u00a9" -> "(C)", // COPYRIGHT SIGN
      "\u00ae" -> "(R)", // REGISTERED SIGN
      "\u2122" -> "(TM)" // TRADE MARK SIGN
    )

    // These are some characters that we want to normalize
    // but we have to wait until after the NFKC normalization has concluded.
    // If there is no reason to map a character before the NFKC normalization then do it here.
    // https://unicode-org.github.io/cldr-staging/charts/37/supplemental/character_fallback_substitutions.html
    val postMapping: Map[String, String] = Map(
      "\u0060" -> "'",   // GRAVE ACCENT
      "\u00ab" -> "<<",  // LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
      "\u00b4" -> "'",   // ACUTE ACCENT
      "\u00bb" -> ">>",  // RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
      "\u00c6" -> "AE",  // LATIN CAPITAL LETTER AE
      "\u00d7" -> "x",   // MULTIPLICATION SIGN
      "\u00e6" -> "ae",  // LATIN SMALL LETTER AE
      "\u0152" -> "OE",  // LATIN CAPITAL LIGATURE OE
      "\u0153" -> "oe",  // LATIN SMALL LIGATURE OE
      "\u0192" -> "f",   // LATIN SMALL LETTER F WITH HOOK
      "\u02c6" -> "^",   // MODIFIER LETTER CIRCUMFLEX ACCENT
      "\u2010" -> "-",   // HYPHEN
      "\u2013" -> "--",  // EN DASH
      "\u2014" -> "---", // EM DASH
      "\u2018" -> "'",   // LEFT SINGLE QUOTATION MARK
      "\u2019" -> "'",   // RIGHT SINGLE QUOTATION MARK
      "\u201a" -> "'",   // SINGLE LOW-9 QUOTATION MARK
      "\u201c" -> "\"",  // LEFT DOUBLE QUOTATION MARK
      "\u201d" -> "\"",  // RIGHT DOUBLE QUOTATION MARK
      "\u201e" -> "\"",  // DOUBLE LOW-9 QUOTATION MARK
      "\u2022" -> "-",   // BULLET
      "\u2023" -> "-",   // TRIANGULAR BULLET
      "\u2032" -> "'",   // PRIME
      "\u2035" -> "'",   // REVERSED PRIME
      "\u2039" -> "<",   // SINGLE LEFT-POINTING ANGLE QUOTATION MARK
      "\u203a" -> ">",   // SINGLE RIGHT-POINTING ANGLE QUOTATION MARK
      "\u2043" -> "-",   // HYPHEN BULLET
      "\u2044" -> "/",   // FRACTION SLASH
      "\u2190" -> "<-",  // LEFTWARDS ARROW
      "\u2192" -> "->",  // RIGHTWARDS ARROW
      "\u2194" -> "<->", // LEFT RIGHT ARROW
      "\u21d0" -> "<=",  // LEFTWARDS DOUBLE ARROW
      "\u21d2" -> "=>",  // RIGHTWARDS DOUBLE ARROW
      "\u21d4" -> "<=>", // LEFT RIGHT DOUBLE ARROW
      "\u2212" -> "-",   // MINUS SIGN
      "\u25e6" -> "-",   // WHITE BULLET
      "\u3008" -> "<",   // LEFT ANGLE BRACKET
      "\u3009" -> ">",   // RIGHT ANGLE BRACKET
      "\u300a" -> "<<",  // LEFT DOUBLE ANGLE BRACKET
      "\u300b" -> ">>"   // RIGHT DOUBLE ANGLE BRACKET
    )

  }

}
