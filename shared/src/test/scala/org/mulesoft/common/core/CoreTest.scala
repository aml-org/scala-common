package org.mulesoft.common.core

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

/**
  * Test Core Methods.
  */
trait CoreTest extends AnyFunSuite with Matchers {

  test("basic strings") {
    val s1 = "aaaxxxa"
    s1.count('a') shouldBe 4
    s1.notNull shouldBe s1
    s1.decode shouldBe s1
    s1.encode shouldBe s1
    s1.nonNullNorEmpty shouldBe true
    s1.isNullOrEmpty shouldBe false

    s1 equalsIgnoreSpaces s1 shouldBe true
    s1 equalsIgnoreSpaces " aa a xxx   a" shouldBe true
    " aa a xxx   a" equalsIgnoreSpaces s1 shouldBe true
    " aa a xx   a" equalsIgnoreSpaces s1 shouldBe false
    " aa a xxx   a   b" equalsIgnoreSpaces s1 shouldBe false

    val s2: String = null
    s2.count('a') shouldBe 0
    s2.notNull shouldBe ""
    s2.decode shouldBe null
    s2.encode shouldBe null
    s2.nonNullNorEmpty shouldBe false
    s2.isNullOrEmpty shouldBe true

    val s3 = ""
    s3.count('a') shouldBe 0
    s3.notNull shouldBe ""
    s3.decode shouldBe ""
    s3.encode shouldBe ""
    s3.nonNullNorEmpty shouldBe false
    s3.isNullOrEmpty shouldBe true
  }
  test("encoded strings") {
    "ab\"c".encode shouldBe "ab\\\"c"

    val code = "a\\u00F3\\\\\\b\\n\\r\\t\\fpi\\u03A0\\u13A0\\u0000quote\\\"\\u0001\\u001F"
    code.decode shouldBe "aó\\\b\n\r\t\fpi\u03A0\u13A0\u0000quote\"\u0001\u001F"
    val encoded = code.decode.encode
    encoded shouldBe code
  }

  test("encoded non ascii strings") {
    "こんにちは".encode(encodeNonAscii = false) shouldBe "こんにちは"
    "こんにちは".encode shouldBe "\\u3053\\u3093\\u306B\\u3061\\u306F"
  }

  test("decode ascii nul character") {
    Char.MinValue.toString.decode.encode shouldBe "\\u0000"
  }

  test("extended decode") {
    "\\x1B\\U0001f600".decode shouldBe "\u001B😀"
    "\\v\\e\\N".decode shouldBe "\u000B\u001B\u0085"
    "\\_\\L\\P".decode shouldBe "\u00A0\u2028\u2029"
    "\\a".decode shouldBe 7.toChar.toString + ""
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
  test("add quotes") {
    val unquoted = "aaa"
    val quoted   = "\"aaa\""

    unquoted.quoted.shouldBe(quoted)
    quoted.quoted.shouldBe(quoted)
  }

  test("strip spaces") {
    "Hello World".stripSpaces shouldBe "HelloWorld"
    "Hello".stripSpaces shouldBe "Hello"
    " Hello    World ".stripSpaces shouldBe "HelloWorld"
  }

  test("chars") {
    '0'.isHexDigit shouldBe true
    'C'.isHexDigit shouldBe true
    'c'.isHexDigit shouldBe true
    'x'.isHexDigit shouldBe false

    ' ' repeat 0 shouldBe ""
    '!' repeat 5 shouldBe "!!!!!"
  }

}
