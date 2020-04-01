package ai.lum.common

import java.io.File
import org.scalatest._
import FileUtils._

class TestFileUtils extends FlatSpec with Matchers {

  "FileUtils" should "write a string to a file" in {
    val file = File.createTempFile("TestFileUtils", ".txt")
    val text = "This is a test."
    file.writeString(text)
    file.readString() shouldEqual text
  }

  it should "write a string to a compressed file" in {
    val file = File.createTempFile("TestFileUtils", ".txt.gz")
    val text = "This is a test."
    file.writeString(text)
    file.readString() shouldEqual text
  }

  it should "write smaller files when compressed" in {
    val classLoader = getClass().getClassLoader()
    val original = new File(classLoader.getResource("frankenstein.txt").getFile())
    val text = original.readString()
    val compressed = File.createTempFile("TestFileUtils", ".txt.gz")
    compressed.writeString(text)
    compressed.size should be < original.size
  }

}
