package org.mulesoft.common.io

import scala.concurrent.ExecutionContext

/**
  * IO Tests
  */
class JsAsyncIoTest extends IoAsyncTest {

  override implicit val executionContext: ExecutionContext = scala.scalajs.concurrent.JSExecutionContext.queue

  def fs: FileSystem = Fs
}
