package ai.lum.common

import java.io.File
import java.nio.file.Files

import ai.lum.common.FileUtils._
import org.scalatest._

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

  it should "make a sibling file" in {
    val file = File.createTempFile("TestFileUtils", ".txt")
    val sibling = file.mkSibling("siblingFileUtils.txt")
    sibling.getParent should equal(file.getParent)
    sibling.getName should equal("siblingFileUtils.txt")
  }

  it should "make a child file" in {
    val file = Files.createTempDirectory("testFileUtils").toFile
    val child = file.mkChild("child.txt")
    child.getParent should equal(file.getAbsolutePath)
  }

  it should "not make a child if not a dir" in {
    val file = File.createTempFile("TestFileUtils", ".txt")
    a [RuntimeException] shouldBe thrownBy(file.mkChild("child.txt"))
  }

  it should "make a parallel file" in {
    val temp = Files.createTempDirectory("alpha").toFile.getAbsolutePath
    val a = new File(temp, "alpha")
    val ab = new File(a, "bravo")
    val abc = new File(ab, "charlie")
    Files.createDirectories(abc.toPath)

    val file = new File(abc, "test.txt")
    file.getPath should endWith ("alpha/bravo/charlie/test.txt")
    val parallel = file.mkParallel("bravo", "delta")
    parallel.getPath should endWith ("alpha/delta/charlie/test.txt")
  }

  it should "not make a parallel file if there is nothing to replace" in {
    val temp = Files.createTempDirectory("alpha").toFile.getAbsolutePath
    val a = new File(temp, "alpha")
    val ab = new File(a, "bravo")
    val abc = new File(ab, "charlie")
    Files.createDirectories(abc.toPath)

    val file = new File(abc, "test.txt")
    file.getPath should endWith ("alpha/bravo/charlie/test.txt")
    an [RuntimeException] shouldBe thrownBy(file.mkParallel("foxtrot", "delta"))
  }
}
