package ai.lum.common

import org.scalatest._
import StringUtils._

class TestStringUtils extends FlatSpec with Matchers {

  "Unicode normalization" should "normalize unicode strings" in {
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

  it should "normalize mu" in {
    val mu1 = "\u00b5"
    val mu2 = "\u03bc"
    val Mu  = "\u039c"
    mu1 should not equal mu2
    mu1 should not equal Mu
    mu2 should not equal Mu
    mu1.normalizeUnicode shouldEqual mu2.normalizeUnicode
    mu1.normalizeUnicode should not equal Mu.normalizeUnicode
    mu2.normalizeUnicode should not equal Mu.normalizeUnicode
    mu1.normalizeUnicodeAggressively shouldEqual mu2.normalizeUnicodeAggressively
    mu1.normalizeUnicodeAggressively shouldEqual Mu.normalizeUnicodeAggressively
    mu2.normalizeUnicodeAggressively shouldEqual Mu.normalizeUnicodeAggressively
  }

  it should "convert summation to sigma" in {
    val s1 = "\u2211"
    val s2 = "\u03a3"
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

  it should "remove diacritics from composed glyphs" in {
    val s1 = "\u01c6"
    val s2 = "dz"
    s1 should not equal s2
    s1.normalizeUnicode should not equal s2.normalizeUnicode
    s1.normalizeUnicodeAggressively shouldEqual s2.normalizeUnicodeAggressively
  }

  it should "normalize backticks" in {
    val s1 = "Nelson Mandela's health `unstable'"
    val s2 = "Nelson Mandela's health 'unstable'"
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

  it should "replace arrows" in {
    val s1 = "→ ← ↔ ⇒ ⇐ ⇔ » « – — 640×480 © ™ ® “He thought 'It's a man's world'…”"
    val s2 = "-> <- <-> => <= <=> >> << -- --- 640x480 (c) (tm) (r) \"He thought 'It's a man's world'...\""
    s1 should not equal s2
    s1.normalizeUnicode should not equal s2.normalizeUnicode
    s1.normalizeUnicodeAggressively shouldEqual s2.normalizeUnicodeAggressively
  }

}
