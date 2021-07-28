package org.mulesoft.common.core

import org.mulesoft.common.functional.MonadInstances._
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.funsuite.AsyncFunSuite

trait CachedFunctionTest extends AsyncFunSuite {
  test("Cache proxy with context") {
    val operation                                   = (f: Float) => Option(Math.nextUp(f))
    val proxy: CachedFunction[Float, Float, Option] = CachedFunction.fromMonadic(operation)

    val areEqual = for {
      random1 <- proxy.runCached(1.0f)
      random2 <- proxy.runCached(1.0f)
    } yield {
      random1 shouldEqual random2 // Despite being random the cached result should be the same
    }

    areEqual.getOrElse(fail())
  }

  test("Cache proxy without context") {
    val operation                                     = (f: Float) => Math.nextUp(f)
    val proxy: CachedFunction[Float, Float, Identity] = CachedFunction.from(operation)

    val random1 = proxy.runCached(1.0f)
    val random2 = proxy.runCached(1.0f)

    random1 shouldEqual random2 // Despite being random the cached result should be the same
  }
}
