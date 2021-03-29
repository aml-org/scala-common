package org.mulesoft.common.core

import org.mulesoft.common.functional.Monad
import org.mulesoft.common.functional.MonadInstances.Identity

import scala.language.higherKinds

/** Runs an operation proxied through a cache
  * @tparam I input type of the operation
  * @tparam O output type of the operation
  * @tparam C context for the output type (e.g. Option)
  */
trait CacheProxy[I, O, C[_]] {
  val cache: Cache[I, O] = Cache.empty[I, O]
  /**
    * Executes the [[run]] operation with caching. On cache hit returns the cached result it otherwise execute run and
    * store the result in the cache.
    * @param i input for the [[run]] operation
    * @param monad monad for mapping over the result context
    * @return
    */
  def runCached(i: I)(implicit monad: Monad[C]): C[O] =
    cache
      .get(i)
      .map(monad.pure)
      .getOrElse {
        val resultContext = run(i)
        monad.map(resultContext)(result => {
          cache.put(i, result)
          result
        })
      }

  def run: I => C[O]
}

object CacheProxy {
  def forMonadic[I, O, C[_]](fn: I => C[O]): CacheProxy[I, O, C] = new CacheProxy[I, O, C] {
    override def run: I => C[O] = fn
  }

  def `for`[I, O](fn: I => Identity[O]): CacheProxy[I, O, Identity] = new CacheProxy[I, O, Identity] {
    override def run: I => Identity[O] = fn
  }
}
