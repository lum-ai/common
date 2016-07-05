/** Copyright 2016 lum.ai
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

    def stripAccents: String = ApacheStringUtils.stripAccents(str)

    def escapeCsv: String = StringEscapeUtils.escapeCsv(str)
    def unescapeCsv: String = StringEscapeUtils.unescapeCsv(str)

    def escapeHtml: String = StringEscapeUtils.escapeHtml4(str)
    def unescapeHtml: String = StringEscapeUtils.unescapeHtml4(str)

    def escapeJava: String = StringEscapeUtils.escapeJava(str)
    def unescapeJava: String = StringEscapeUtils.unescapeJava(str)

    def escapeJson: String = StringEscapeUtils.escapeJson(str)
    def unescapeJson: String = StringEscapeUtils.unescapeJson(str)

    def escapeXml: String = StringEscapeUtils.escapeXml10(str)
    def unescapeXml: String = StringEscapeUtils.unescapeXml(str)

    def splitOnWhitespace: Array[String] = ApacheStringUtils.splitByWholeSeparatorPreserveAllTokens(str, null)

    def normalizeSpace: String = ApacheStringUtils.normalizeSpace(str)

    def normalize: String = normalizeSpace.stripAccents.toLowerCase

    def splitCamelCase: Array[String] = ApacheStringUtils.splitByCharacterTypeCamelCase(str)

    def abbreviate(maxWidth: Int): String = ApacheStringUtils.abbreviate(str, maxWidth)

    def textwrap(wrapLength: Int): String = WordUtils.wrap(str, wrapLength)

  }

}
