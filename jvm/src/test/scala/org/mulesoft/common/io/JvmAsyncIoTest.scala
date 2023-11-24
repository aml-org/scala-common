package org.mulesoft.common.io

import scala.concurrent.ExecutionContext

/**
  * IO Tests
  */
class JvmAsyncIoTest extends IoAsyncTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  def fs: FileSystem = Fs

}
