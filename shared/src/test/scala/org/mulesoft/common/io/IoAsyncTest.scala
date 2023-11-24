package org.mulesoft.common.io

import org.scalatest.funsuite.AsyncFunSuite

import scala.concurrent.ExecutionContext

/**
  * IO Tests
  */
trait IoAsyncTest extends AsyncFunSuite with BaseIoTest {

  test("read") {
    val dataDir   = fs.asyncFile(dirName)
    val hello     = dataDir / helloFileName
    val helloIso  = hello withExt "iso"
    val helloPath = dataDir.path + fs.separatorChar + helloFileName
    hello.path shouldBe helloPath
    hello.toString shouldBe helloPath

    hello.parent shouldBe dataDir.path
    hello.name shouldBe helloFileName

    for {
      b <- dataDir.exists
      if b
      d <- dataDir.isDirectory
      if d
      f <- hello.isFile
      if f
      s1 <- hello.read()
      s2 <- helloIso read LatinEncoding
    } yield (s1.toString, s2.toString) shouldBe (helloString, helloString)
  }

  test("write") {
    val targetDir = fs.syncFile(targetDirName).async
    val hello     = targetDir / helloFileName
    val helloIso  = targetDir / helloIsoFileName

    for {
      _  <- targetDir.delete
      _  <- targetDir.mkdir
      _  <- hello write helloString
      _  <- helloIso.write(helloString, LatinEncoding)
      s1 <- hello.read()
      s2 <- helloIso read LatinEncoding
      l  <- targetDir.list
      _  <- hello.delete
      _  <- helloIso.delete
      b  <- hello.exists
      b2 <- hello.isDirectory
      if !b && !b2
      _ <- helloIso.delete
    } yield assert(s1.toString == helloString && s2.toString == helloString && l.sorted.toList == dirList)
  }

  test("errors") {
    recoverToSucceededIf[Exception] {
      (fs.asyncFile(targetDirName) / helloFileName).read()
    }
  }
}
