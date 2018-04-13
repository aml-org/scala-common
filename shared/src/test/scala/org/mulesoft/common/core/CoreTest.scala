package org.mulesoft.common.core

import org.scalatest.{FunSuite, Matchers}

/**
  * Test Core Methods.
  */
trait CoreTest extends FunSuite with Matchers {

    test("basic strings") {
        val s1 = "aaaxxxa"
        s1.count('a') shouldBe 4
        s1.notNull shouldBe s1
        s1.decode shouldBe s1
        s1.encode shouldBe s1

        s1 equalsIgnoreSpaces   s1 shouldBe true
        s1 equalsIgnoreSpaces  " aa a xxx   a" shouldBe true
        " aa a xxx   a" equalsIgnoreSpaces s1 shouldBe true
        " aa a xx   a" equalsIgnoreSpaces s1 shouldBe false
        " aa a xxx   a   b" equalsIgnoreSpaces s1 shouldBe false

        val s2:String = null
        s2.count('a') shouldBe 0
        s2.notNull shouldBe ""
        s2.decode shouldBe null
        s2.encode shouldBe null

        val s3 = ""
        s3.count('a') shouldBe 0
        s3.notNull shouldBe ""
        s3.decode shouldBe ""
        s3.encode shouldBe ""
    }
    test("encoded strings") {
        val code = "a\\u00F3\\\\\\b\\n\\r\\t\\fpi\\u03A0\\0quote\\\""
        code.decode shouldBe "aó\\\b\n\r\t\fpi\u03A0\u0000quote\""
        val encoded = code.decode.encode
        encoded shouldBe code
    }
    test("extended decode") {
        "\\x1B\\U0001f600".decode shouldBe "\u001B😀"
        "\\v\\e\\N".decode shouldBe "\u000B\u001B\u0085"
        "\\_\\L\\P".decode shouldBe "\u00A0\u2028\u2029"
        "\\a".decode shouldBe 7.toChar + ""
    }
    test("errors") {
        an[IllegalArgumentException] should be thrownBy "\\u7".decode
        "Ho\\u7la mundo".decode(ignoreErrors = true) shouldBe "Hola mundo"
        "Ho\\u7l".decode(ignoreErrors = true) shouldBe "Hol"
        "Ho\\u07".decode(ignoreErrors = true) shouldBe "Ho"
    }
    test("count while") {
        val str = "aaaacccc"
        countWhile(str(_) == 'a') shouldBe 4
    }
    test("replace ext") {
        "aaa.xxx" replaceExtension "" shouldBe "aaa"
        "aaa" replaceExtension "bb" shouldBe "aaa.bb"
        "x.y.z" replaceExtension "a" shouldBe "x.y.a"
    }
}
