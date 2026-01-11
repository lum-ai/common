package ai.lum.common

import TryWithResources.using

class TestTryWithResources extends Test {

  import TestTryWithResources._

  "TryWithResources" should "not throw an exception" in {
    val closeable = new Closeable
    noException should be thrownBy using (closeable) { c =>
      c.doSomething()
    }
    closeable.n should be (1)
  }

  it should "throw an exception" in {
    val closeable = new CloseableWithException
    val thrown = the [Exception] thrownBy using (closeable) { c =>
      c.doSomething()
    }
    thrown.getMessage() shouldEqual ("doSomething")
    thrown.getSuppressed() should have length (0)
    closeable.n should be (1)
  }

  it should "throw a fatal exception" in {
    val closeable = new CloseableWithFatalException
    val thrown = the [InterruptedException] thrownBy using (closeable) { c =>
      c.doSomething()
    }
    thrown.getMessage() shouldEqual ("doSomething")
    thrown.getSuppressed() should have length (0)
    closeable.n should be (1)
  }

  it should "throw an exception during closing" in {
    val closeable = new CloseableWithCloseException
    val thrown = the [Exception] thrownBy using (closeable) { c =>
      c.doSomething()
    }
    thrown.getMessage() shouldEqual ("close")
    thrown.getSuppressed() should have length (0)
    closeable.n should be (1)
  }

  it should "throw a fatal exception during closing" in {
    val closeable = new CloseableWithCloseFatalException
    val thrown = the [Exception] thrownBy using (closeable) { c =>
      c.doSomething()
    }
    thrown.getMessage() shouldEqual ("close")
    thrown.getSuppressed() should have length (0)
    closeable.n should be (1)
  }

  it should "suppress exception when closing fatal exception occurs" in {
    val closeable = new CloseableWithExceptionAndCloseFatalException
    val thrown = the [InterruptedException] thrownBy using (closeable) { c =>
      c.doSomething()
    }
    thrown.getMessage() shouldEqual ("close")
    thrown.getSuppressed() should have length (1)
    thrown.getSuppressed()(0).getMessage() shouldEqual ("doSomething")
    closeable.n should be (1)
  }

  it should "suppress closing exception when fatal exception occurs" in {
    val closeable = new CloseableWithFatalExceptionAndCloseException
    val thrown = the [InterruptedException] thrownBy using (closeable) { c =>
      c.doSomething()
    }
    thrown.getMessage() shouldEqual ("doSomething")
    thrown.getSuppressed() should have length (1)
    thrown.getSuppressed()(0).getMessage() shouldEqual ("close")
    closeable.n should be (1)
  }

  it should "suppress closing fatal exception when fatal exception occurs" in {
    val closeable = new CloseableWithFatalExceptionAndCloseFatalException
    val thrown = the [InterruptedException] thrownBy using (closeable) { c =>
      c.doSomething()
    }
    thrown.getMessage() shouldEqual ("doSomething")
    thrown.getSuppressed() should have length (1)
    thrown.getSuppressed()(0).getMessage() shouldEqual ("close")
    closeable.n should be (1)
  }

}

object TestTryWithResources {

  class Closeable {
    var n = 0
    def close(): Unit = {
      n += 1
    }
    def doSomething(): Unit = {}
  }

  class CloseableWithException {
    var n = 0
    def close(): Unit = {
      n += 1
    }
    def doSomething(): Unit = {
      throw new Exception("doSomething")
    }
  }

  class CloseableWithFatalException {
    var n = 0
    def close(): Unit = {
      n += 1
    }
    def doSomething(): Unit = {
      throw new InterruptedException("doSomething")
    }
  }

  class CloseableWithCloseException {
    var n = 0
    def close(): Unit = {
      n += 1
      throw new Exception("close")
    }
    def doSomething(): Unit = {}
  }

  class CloseableWithCloseFatalException {
    var n = 0
    def close(): Unit = {
      n += 1
      throw new InterruptedException("close")
    }
    def doSomething(): Unit = {}
  }

  class CloseableWithFatalExceptionAndCloseException {
    var n = 0
    def close(): Unit = {
      n += 1
      throw new Exception("close")
    }
    def doSomething(): Unit = {
      throw new InterruptedException("doSomething")
    }
  }

  class CloseableWithExceptionAndCloseFatalException {
    var n = 0
    def close(): Unit = {
      n += 1
      throw new InterruptedException("close")
    }
    def doSomething(): Unit = {
      throw new Exception("doSomething")
    }
  }

  class CloseableWithFatalExceptionAndCloseFatalException {
    var n = 0
    def close(): Unit = {
      n += 1
      throw new InterruptedException("close")
    }
    def doSomething(): Unit = {
      throw new InterruptedException("doSomething")
    }
  }

}
