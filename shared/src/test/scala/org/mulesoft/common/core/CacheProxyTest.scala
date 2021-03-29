package org.mulesoft.common.core

import org.mulesoft.common.functional.MonadInstances._
import org.scalatest.AsyncFunSuite
import org.scalatest.Matchers.convertToAnyShouldWrapper

trait CacheProxyTest extends AsyncFunSuite {
  test("Cache proxy with context") {
    val operation                               = (f: Float) => Option(Math.nextUp(f))
    val proxy: CacheProxy[Float, Float, Option] = CacheProxy.forMonadic(operation)

    val areEqual = for {
      random1 <- proxy.runCached(1.0f)
      random2 <- proxy.runCached(1.0f)
    } yield {
      random1 shouldEqual random2 // Despite being random the cached result should be the same
    }

    areEqual.getOrElse(fail())
  }

  test("Cache proxy without context") {
    val operation                                 = (f: Float) => Math.nextUp(f)
    val proxy: CacheProxy[Float, Float, Identity] = CacheProxy.`for`(operation)

    val random1 = proxy.runCached(1.0f)
    val random2 = proxy.runCached(1.0f)

    random1 shouldEqual random2 // Despite being random the cached result should be the same
  }
}
