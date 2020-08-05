package ai.lum.common

import scala.language.implicitConversions

object RegexUtils {

  val metacharacters = "<([{\\^-=$!|]})?*+.>"

  /**
    * Returns a literal pattern String for the specified String.
    * This method differs from scala.util.matching.Regex.quote()
    * in that it adds backslashes to regex metacharacters instead
    * of surrounding the string with \Q and \E
    *
    * @param s
    * @return
    */
  def quote(s: String): String = {
    s.map(c => if (metacharacters contains c) s"\\$c" else c).mkString
  }

  /**
    * Makes regexes for bracketed strings.
    *
    * @param pairs a string of delimeters, e.g., (){}[]<>
    * @param escapes a string with the escape characters
    * @return
    */
  def mkCharBracketed(pairs: String, escapes: String = "\\"): String = {
    require(pairs.nonEmpty, "missing pairs")
    require(pairs.size % 2 == 0, "incomplete pair")
    implicit def asPair(a: Array[String]): (String, String) = (a(0), a(1))
    val (open, close) = pairs.split("").grouped(2).toArray.unzip
    mkCharDelimited(open.mkString, escapes, close.mkString)
  }

  /**
    * Makes regexes for delimited strings.
    * Uses non-capturing groups and possessive quantifiers for efficiency.
    *
    * @param delimiters
    * @param escapes
    * @param closeDelimiters
    * @return
    */
  def mkCharDelimited(
      delimiters: String,
      escapes: String = "\\",
      closeDelimiters: String = ""
  ): String = {
    require(delimiters.nonEmpty, "missing delimirers")
    val numDelimiters = delimiters.size
    // get escape chars
    val escs = escapes.size match {
      case 0 => "" // no escape char
      case 1 => escapes * numDelimiters // same escape char for all delimiters
      case `numDelimiters` => escapes // distinct escape char per delimiter
      case _ => throw new IllegalArgumentException("invalid escapes")
    }
    // get close delimiters
    val cdels = closeDelimiters.size match {
      case 0 => delimiters // close delimiters default to open delimiters
      case `numDelimiters` => closeDelimiters // distinct close delimiter per open delimiter
      case _ => throw new IllegalArgumentException("illegal close delimiters")
    }
    // get patterns for each open/close delimiter pair
    val patterns = for (i <- delimiters.indices) yield {
      // quote chars for regex compatibility
      val del = quote(delimiters(i).toString)
      val cdel = quote(cdels(i).toString)
      val esc = if (escs.nonEmpty) quote(escs(i).toString) else ""
      // make pattern
      if (cdel == esc) {
        // escape is equal to close delimiter
        s"$del[^$cdel]*+(?:$cdel$cdel[^$cdel]*+)*+$cdel"
      } else if (esc.nonEmpty) {
        // escape char can be used on to escape any char
        s"$del[^$esc$cdel]*+(?:$esc.[^$esc$cdel]*+)*+$cdel"
      } else {
        // no escape char
        s"$del[^$cdel]*+$cdel"
      }
    }
    // return single regex for all patterns
    patterns.mkString("|")
  }

}
