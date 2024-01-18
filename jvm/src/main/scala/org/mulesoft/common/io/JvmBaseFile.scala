package org.mulesoft.common.io

import scala.concurrent.ExecutionContext

trait JvmBaseFile extends File {

  override val global: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

}
