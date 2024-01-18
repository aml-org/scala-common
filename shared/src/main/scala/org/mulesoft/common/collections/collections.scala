package org.mulesoft.common

import scala.collection.mutable.ArrayBuffer
import scala.collection.{BuildFrom, IterableOps, immutable, mutable}
import scala.reflect.ClassTag

package object collections {

  /** Wrapper for type filtering for collections of type T[A]
    * @param collection
    *   wrapped collection
    * @tparam T
    *   collection type
    * @tparam A
    *   collection member type
    */
  implicit class FilterType[A, T[A] <: IterableOps[A, T, T[A]]](collection: T[A]) {

    /** Filters elements from collection by type B
      * @param tag
      *   implicit class tag to workaround type erasure
      * @param bf
      *   builds the same input collection type T for the output T[B]
      * @tparam B
      *   type to filter
      * @return
      *   collection with filtered members of collection of type B
      */
    def filterType[B](implicit tag: ClassTag[B], bf: BuildFrom[T[A], B, T[B]]): T[B] = collection.collect {
      case element: B => element
    }
  }

  /** Utility methods to group collections
    */
  implicit class Group[A](val t: IterableOnce[A]) {

    /** groupBy method present in Scala 2.12.12
      */
    def legacyGroupBy[K](f: A => K): immutable.Map[K, ArrayBuffer[A]] = {
      val m = mutable.Map.empty[K, mutable.Builder[A, ArrayBuffer[A]]]
      t.iterator.foreach(elem => {
        val key  = f(elem)
        val bldr = m.getOrElseUpdate(key, mutable.ArrayBuffer.newBuilder[A])
        bldr += elem
      })
      val b = immutable.Map.newBuilder[K, ArrayBuffer[A]]
      for ((k, v) <- m)
        b += ((k, v.result()))

      b.result()
    }

  }

}
