package ai.lum.common

import org.scalatest._
import TryWithResources.using

class TestTryWithResources extends FlatSpec with Matchers {

  import TestTryWithResources._

  "TryWithResources" should "not throw an exception" in {
    noException should be thrownBy using (new Closeable) { c =>
      c.doSomething()
    }
  }

  it should "throw an exception" in {
    val thrown = the [Exception] thrownBy using (new CloseableWithException) { c =>
      c.doSomething()
    }
    thrown.getSuppressed() should have length (0)
  }

  it should "throw a fatal exception" in {
    val thrown = the [InterruptedException] thrownBy using (new CloseableWithFatalException) { c =>
      c.doSomething()
    }
    thrown.getSuppressed() should have length (0)
  }

  it should "throw an exception during closing" in {
    val thrown = the [Exception] thrownBy using (new CloseableWithCloseException) { c =>
      c.doSomething()
    }
    thrown.getSuppressed() should have length (0)
  }

  it should "throw a fatal exception during closing" in {
    val thrown = the [Exception] thrownBy using (new CloseableWithCloseFatalException) { c =>
      c.doSomething()
    }
    thrown.getSuppressed() should have length (0)
  }

  it should "suppress exception when closing fatal exception occurs" in {
    val thrown = the [InterruptedException] thrownBy using (new CloseableWithExceptionAndCloseFatalException) { c =>
      c.doSomething()
    }
    thrown.getSuppressed() should have length (1)
  }

  it should "suppress closing exception when fatal exception occurs" in {
    val thrown = the [InterruptedException] thrownBy using (new CloseableWithExceptionAndCloseFatalException) { c =>
      c.doSomething()
    }
    thrown.getSuppressed() should have length (1)
  }

}

object TestTryWithResources {

  class Closeable {
    def close(): Unit = {}
    def doSomething(): Unit = {}
  }

  class CloseableWithException {
    def close(): Unit = {}
    def doSomething(): Unit = {
      throw new Exception
    }
  }

  class CloseableWithFatalException {
    def close(): Unit = {}
    def doSomething(): Unit = {
      throw new InterruptedException
    }
  }

  class CloseableWithCloseException {
    def close(): Unit = {
      throw new Exception
    }
    def doSomething(): Unit = {}
  }

  class CloseableWithCloseFatalException {
    def close(): Unit = {
      throw new InterruptedException
    }
    def doSomething(): Unit = {}
  }

  class CloseableWithFatalExceptionAndCloseException {
    def close(): Unit = {
      throw new Exception
    }
    def doSomething(): Unit = {
      throw new InterruptedException
    }
  }

  class CloseableWithExceptionAndCloseFatalException {
    def close(): Unit = {
      throw new InterruptedException
    }
    def doSomething(): Unit = {
      throw new Exception
    }
  }

}
