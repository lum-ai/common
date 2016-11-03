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

import java.io.{ File, FileFilter }
import java.nio.charset.Charset
import java.nio.charset.{ StandardCharsets => JavaStandardCharsets }
import org.apache.commons.io.{ FileUtils => IOFileUtils, FilenameUtils, IOCase }
import org.apache.commons.io.filefilter.{ RegexFileFilter, WildcardFileFilter }

object FileUtils {

  /** Constant definitions for the standard Charsets.
   *  These charsets are guaranteed to be available
   *  on every implementation of the Java platform.
   */
  object StandardCharsets {
    val US_ASCII   = JavaStandardCharsets.US_ASCII
    val ISO_8859_1 = JavaStandardCharsets.ISO_8859_1
    val UTF_8      = JavaStandardCharsets.UTF_8
    val UTF_16     = JavaStandardCharsets.UTF_16
    val UTF_16BE   = JavaStandardCharsets.UTF_16BE
    val UTF_16LE   = JavaStandardCharsets.UTF_16LE
  }

  implicit class FileWrapper(val file: File) extends AnyVal {

    /** Gets the base name, minus the full path and extension. */
    def getBaseName(): String = FilenameUtils.getBaseName(file.getPath())

    /** Gets the extension of a file. */
    def getExtension(): String = FilenameUtils.getExtension(file.getPath())

    /** Checks a file to see if it matches the specified wildcard matcher allowing control over case-sensitivity. */
    def wildcardMatch(wildcardMatcher: String, caseSensitive: Boolean = true): Boolean = {
      val caseSensitivity = if (caseSensitive) IOCase.SENSITIVE else IOCase.INSENSITIVE
      FilenameUtils.wildcardMatch(file.getPath(), wildcardMatcher, caseSensitivity)
    }

    def listFilesByRegex(pattern: String, caseSensitive: Boolean = true): Array[File] = {
      val caseSensitivity = if (caseSensitive) IOCase.SENSITIVE else IOCase.INSENSITIVE
      val fileFilter: FileFilter = new RegexFileFilter(pattern, caseSensitivity)
      file.listFiles(fileFilter)
    }

    def listFilesByWildcard(wildcard: String, caseSensitive: Boolean = true): Array[File] = {
      val caseSensitivity = if (caseSensitive) IOCase.SENSITIVE else IOCase.INSENSITIVE
      val fileFilter: FileFilter = new WildcardFileFilter(wildcard, caseSensitivity)
      file.listFiles(fileFilter)
    }

    def readString(charset: String): String = IOFileUtils.readFileToString(file, charset)

    def readString(charset: Charset = null): String = IOFileUtils.readFileToString(file, charset)

    def readByteArray(): Array[Byte] = IOFileUtils.readFileToByteArray(file)

    def writeString(string: String, charset: String): Unit = {
      IOFileUtils.writeStringToFile(file, string, charset)
    }

    def writeString(string: String, charset: String, append: Boolean): Unit = {
      IOFileUtils.writeStringToFile(file, string, charset, append)
    }

    def writeString(string: String, charset: Charset): Unit = {
      IOFileUtils.writeStringToFile(file, string, charset)
    }

    def writeString(string: String, charset: Charset = null, append: Boolean = false): Unit = {
      IOFileUtils.writeStringToFile(file, string, charset, append)
    }

    def writeByteArray(bytes: Array[Byte]): Unit = {
      IOFileUtils.writeByteArrayToFile(file, bytes)
    }

    def writeByteArray(bytes: Array[Byte], append: Boolean): Unit = {
      IOFileUtils.writeByteArrayToFile(file, bytes, append)
    }

    def writeByteArray(bytes: Array[Byte], off: Int, len: Int, append: Boolean = false): Unit = {
      IOFileUtils.writeByteArrayToFile(file, bytes, off, len, append)
    }

  }

}
