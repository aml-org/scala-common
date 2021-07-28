package org.mulesoft.common.collections

import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.funsuite.AnyFunSuite

trait FilterTypeTest extends AnyFunSuite {
  test("Test simple type filtering") {
    sealed trait Letter
    case class A() extends Letter
    case class B() extends Letter
    case class C() extends Letter

    val collection: Seq[Letter] = Seq(A(), A(), A(), B(), B(), C())

    val as: Seq[A] = collection.filterType[A]
    val bs: Seq[B] = collection.filterType[B]
    val cs: Seq[C] = collection.filterType[C]

    as.size shouldBe 3
    bs.size shouldBe 2
    cs.size shouldBe 1
  }
}
