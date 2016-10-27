package ai.lum.common

import java.io.{ File, FileFilter }
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOCase
import org.apache.commons.io.filefilter.{ RegexFileFilter, WildcardFileFilter }

object FileUtils {

  implicit class FileWrapper(val file: File) extends AnyVal {

    /** Gets the base name, minus the full path and extension. */
    def getBaseName: String = FilenameUtils.getBaseName(file.getPath())

    /** Gets the extension of a file. */
    def getExtension: String = FilenameUtils.getExtension(file.getPath())

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

  }

}
