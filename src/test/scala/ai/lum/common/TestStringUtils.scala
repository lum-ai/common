package ai.lum.common

import org.scalatest._
import StringUtils._

class TestStringUtils extends FlatSpec with Matchers {

  "StringUtils" should "normalize unicode strings" in {
    val s1 = "caf\u00e9"
    val s2 = "cafe\u0301"
    s1 should not equal s2
    s1.normalizeUnicode shouldEqual s2.normalizeUnicode
    s1.normalizeUnicodeAggressively shouldEqual s2.normalizeUnicodeAggressively
  }

  it should "support aggressive normalization" in {
    val s1 = "\u00bd"
    val s2 = "1/2"
    s1 should not equal s2
    s1.normalizeUnicode should not equal s2.normalizeUnicode
    s1.normalizeUnicodeAggressively shouldEqual s2.normalizeUnicodeAggressively
  }

  it should "support casefolding" in {
    val s1 = "\u00df"
    val s2 = "ss"
    s1 should not equal s2
    s1.normalizeUnicode should not equal s2.normalizeUnicode
    s1.normalizeUnicodeAggressively shouldEqual s2.normalizeUnicodeAggressively
  }

  it should "remove diacritics" in {
    val s1 = "caf\u00e9"
    val s2 = "cafe"
    s1 should not equal s2
    s1.normalizeUnicode should not equal s2.normalizeUnicode
    s1.normalizeUnicodeAggressively shouldEqual s2.normalizeUnicodeAggressively
  }

  it should "replace characters" in {
    // example from "Fluent Python"
    val s1 = "“Herr Voß: • ½ cup of Œtker™ caffè latte • bowl of açaí.”"
    val s2 = "\"Herr Voss: - 1/2 cup of OEtker(TM) caffe latte - bowl of acai.\""
    s1 should not equal s2
    s1.normalizeUnicode should not equal s2.normalizeUnicode
    s1.normalizeUnicodeAggressively shouldEqual s2.normalizeUnicodeAggressively
  }

}
