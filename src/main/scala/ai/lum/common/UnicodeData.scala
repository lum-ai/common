package ai.lum.common

import com.ibm.icu.lang.{ UCharacter, UProperty }

object UnicodeData {

  def version: String = {
    UCharacter.getUnicodeVersion().toString()
  }

  def lookup(name: String): Int = {
    UCharacter.getCharFromExtendedName(name)
  }

  def getName(c: Int): String = {
    UCharacter.getExtendedName(c)
  }

  def getCodePoint(c: Char): Int = {
    UCharacter.getCodePoint(c)
  }

  def getCategory(c: Int, short: Boolean = false): String = {
    val nameChoice =
      if (short) UProperty.NameChoice.SHORT
      else UProperty.NameChoice.LONG
    val category = UProperty.GENERAL_CATEGORY_MASK
    val value = UCharacter.getIntPropertyValue(c, category)
    UCharacter.getPropertyValueName(category, value, nameChoice)
  }

  def getNumericValue(c: Int): Double = {
    val n = UCharacter.getUnicodeNumericValue(c)
    if (n == UCharacter.NO_NUMERIC_VALUE) Double.NaN else n
  }

  def toString(c: Int): String = {
    UCharacter.toString(c)
  }

  def isLegal(c: Int): Boolean = {
    UCharacter.isLegal(c)
  }

  def isBaseForm(c: Int): Boolean = {
    UCharacter.isBaseForm(c)
  }

  def isBMP(c: Int): Boolean = {
    UCharacter.isBMP(c)
  }

  def isSupplementary(c: Int): Boolean = {
    UCharacter.isSupplementary(c)
  }

  def isPrintable(c: Int): Boolean = {
    UCharacter.isPrintable(c)
  }

}
