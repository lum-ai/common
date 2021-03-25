package ai.lum.common

import StringUtils._
import DisplayUtils._

class TestStringUtils extends Test {

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

  it should "normalize hyphens" in {
    val s1 = "-"
    val s2 = "\u2010"
    s1 should not equal s2
    s1.normalizeUnicode should not equal s2.normalizeUnicode
    s1.normalizeUnicodeAggressively shouldEqual s2.normalizeUnicodeAggressively
  }

  it should "normalize minus" in {
    val s1 = "-"
    val s2 = "\u2212"
    s1 should not equal s2
    s1.normalizeUnicode should not equal s2.normalizeUnicode
    s1.normalizeUnicodeAggressively shouldEqual s2.normalizeUnicodeAggressively
  }

  it should s"normalize ${"\u00c6".display} and ${"\u1d2d".display}" in {
    val AE = "\u00c6"
    val AE_mod = "\u1d2d"
    AE should not equal AE_mod
    AE.normalizeUnicode shouldEqual AE_mod.normalizeUnicode
    AE.normalizeUnicodeAggressively shouldEqual AE_mod.normalizeUnicodeAggressively
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
