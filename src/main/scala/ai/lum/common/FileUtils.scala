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

import java.io._
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.util.zip.{ GZIPInputStream, GZIPOutputStream }
import org.apache.commons.io.{ FileUtils => IOFileUtils, FilenameUtils, IOCase, IOUtils }
import org.apache.commons.io.filefilter._
import ai.lum.common.TryWithResources.using

object FileUtils {

  implicit class LumAICommonFileWrapper(val file: File) extends AnyVal {

    /** Gets the base name, minus the full path and extension. */
    def getBaseName(): String = FilenameUtils.getBaseName(file.getPath())

    /** Gets the extension of a file. */
    def getExtension(): String = FilenameUtils.getExtension(file.getPath())

    /** Checks a file to see if it matches the specified wildcard matcher allowing control over case-sensitivity. */
    def wildcardMatch(wildcard: String, caseInsensitive: Boolean = false): Boolean = {
      val caseSensitivity = if (caseInsensitive) IOCase.INSENSITIVE else IOCase.SENSITIVE
      FilenameUtils.wildcardMatch(file.getPath(), wildcard, caseSensitivity)
    }

    private def walkFiles(fileFilter: IOFileFilter, dirFilter: IOFileFilter): Iterable[File] = {
      require(file.isDirectory, s"${file.getCanonicalPath()} is not a directory")
      val effectiveFileFilter = FileFilterUtils.and(fileFilter, FileFilterUtils.notFileFilter(DirectoryFileFilter.INSTANCE))
      val effectiveDirFilter = FileFilterUtils.and(dirFilter, DirectoryFileFilter.INSTANCE)
      val filter: FileFilter = FileFilterUtils.or(effectiveFileFilter, effectiveDirFilter)
      def dirWalker(f: File): Iterator[File] = {
        if (f.isDirectory) {
          f.listFiles(filter).toIterator.flatMap(dirWalker)
        } else {
          Iterator(f)
        }
      }
      // return an iterable
      new Iterable[File] {
        def iterator = dirWalker(file)
      }
    }

    def listFilesByRegex(pattern: String, caseInsensitive: Boolean = false, recursive: Boolean = false): Iterable[File] = {
      val caseSensitivity = if (caseInsensitive) IOCase.INSENSITIVE else IOCase.SENSITIVE
      val fileFilter = new RegexFileFilter(pattern, caseSensitivity)
      val dirFilter = if (recursive) TrueFileFilter.INSTANCE else FalseFileFilter.INSTANCE
      walkFiles(fileFilter, dirFilter)
    }

    def listFilesByWildcard(wildcard: String, caseInsensitive: Boolean = false, recursive: Boolean = false): Iterable[File] = {
      val caseSensitivity = if (caseInsensitive) IOCase.INSENSITIVE else IOCase.SENSITIVE
      val fileFilter = new WildcardFileFilter(wildcard, caseSensitivity)
      val dirFilter = if (recursive) TrueFileFilter.INSTANCE else FalseFileFilter.INSTANCE
      walkFiles(fileFilter, dirFilter)
    }

    def listFilesByWildcards(wildcards: Seq[String], caseInsensitive: Boolean = false, recursive: Boolean = false): Iterable[File] = {
      val caseSensitivity = if (caseInsensitive) IOCase.INSENSITIVE else IOCase.SENSITIVE
      val fileFilter = new WildcardFileFilter(wildcards.toArray, caseSensitivity)
      val dirFilter = if (recursive) TrueFileFilter.INSTANCE else FalseFileFilter.INSTANCE
      walkFiles(fileFilter, dirFilter)
    }

    def touch(): Unit = IOFileUtils.touch(file)

    /** Returns the size of the file (in bytes) */
    def size: Long = IOFileUtils.sizeOf(file)

    def sizeAsBigInt: BigInt = IOFileUtils.sizeOfAsBigInteger(file)

    /** Returns an input stream. Don't forget to close it! */
    def toInputStream: BufferedInputStream = new BufferedInputStream(new FileInputStream(file))

    /** Returns an output stream. Don't forget to close it! */
    def toOutputStream: BufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file))

    def readString(charset: Charset = UTF_8, gzipSupport: Boolean = true): String = {
      if (gzipSupport && getExtension().equalsIgnoreCase("gz")) {
        using (new FileInputStream(file)) { fis =>
          using (new BufferedInputStream(fis)) { bis =>
            using (new GZIPInputStream(bis)) { gzis =>
              IOUtils.toString(gzis, charset)
            }
          }
        }
      } else {
        IOFileUtils.readFileToString(file, charset)
      }
    }

    def readByteArray(gzipSupport: Boolean = true): Array[Byte] = {
      if (gzipSupport && getExtension().equalsIgnoreCase("gz")) {
        using (new FileInputStream(file)) { fis =>
          using (new BufferedInputStream(fis)) { bis =>
            using (new GZIPInputStream(bis)) { gzis =>
              IOUtils.toByteArray(gzis)
            }
          }
        }
      } else {
        IOFileUtils.readFileToByteArray(file)
      }
    }

    def writeString(string: String, charset: Charset = UTF_8, append: Boolean = false, gzipSupport: Boolean = true): Unit = {
      if (gzipSupport && getExtension().equalsIgnoreCase("gz")) {
        using (new FileOutputStream(file, append)) { fos =>
          using (new BufferedOutputStream(fos)) { bos =>
            using (new GZIPOutputStream(bos)) { gzos =>
              IOUtils.write(string, gzos, charset)
            }
          }
        }
      } else {
        IOFileUtils.writeStringToFile(file, string, charset, append)
      }
    }

    def writeByteArray(bytes: Array[Byte], append: Boolean = false, gzipSupport: Boolean = true): Unit = {
      if (gzipSupport && getExtension().equalsIgnoreCase("gz")) {
         using (new FileOutputStream(file, append)) { fos =>
          using (new BufferedOutputStream(fos)) { bos =>
            using (new GZIPOutputStream(bos)) { gzos =>
              IOUtils.write(bytes, gzos)
            }
          }
        }
      } else {
        IOFileUtils.writeByteArrayToFile(file, bytes, append)
      }
    }

  }

}
