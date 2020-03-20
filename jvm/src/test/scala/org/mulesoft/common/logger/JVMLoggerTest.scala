package org.mulesoft.common.logger

import org.scalatest.{FunSuite, Matchers}
import java.io.ByteArrayOutputStream

class JVMLoggerTest extends FunSuite with Matchers {

  test("println logger") {
    testLoggerDebug(PrintLnLogger, " DEBUG JVMloggerTest suite:test    Test debug message\n")
  }

  test("muted logger") {
    testLoggerDebug(new MutedLogger(), "")
  }

  test("empty logger") {
    testLoggerDebug(EmptyLogger, "")
  }

  private def testLoggerDebug(logger: Logger, expectedOutput: String) = {
    val outSpy = new ByteArrayOutputStream
    // all printlns in the following block are redirected to outSpy
    Console.withOut(outSpy) {
      logger.debug("Test debug message", "JVMloggerTest suite", "test")
    }
    outSpy.toString shouldBe expectedOutput
  }
}
