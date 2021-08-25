package org.mulesoft.common

import scala.collection.GenTraversableLike
import scala.collection.generic.CanBuildFrom
import scala.reflect.ClassTag
import scala.language.higherKinds

package object collections {

  /**
    * Wrapper for type filtering for collections of type T[A]
    * @param collection wrapped collection
    * @tparam T collection type
    * @tparam A collection member type
    */
  implicit class FilterType[A, T[A] <: GenTraversableLike[A, T[A]]](collection: T[A]) {

    /**
      * Filters elements from collection by type B
      * @param tag implicit class tag to workaround type erasure
      * @param bf builds the same input collection type T for the output T[B]
      * @tparam B type to filter
      * @return collection with filtered members of collection of type B
      */
    def filterType[B](implicit tag: ClassTag[B], bf: CanBuildFrom[T[A], B, T[B]]): T[B] = collection.collect {
      case element: B => element
    }
  }

}
