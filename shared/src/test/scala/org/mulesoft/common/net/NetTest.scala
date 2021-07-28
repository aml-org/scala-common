package org.mulesoft.common.net

import org.mulesoft.common.parse.ParseException
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

/**
  * Test Core Methods.
  */
trait NetTest extends AnyFunSuite with Matchers {

  test("inet address") {
    import InetAddress._

    parseAsIPv4("0.0.0.0").isRight shouldBe true
    parseAsIPv6("0.0.0.0").isRight shouldBe false
    parseAsIPv6("2001:12:0:0:0:0:3257:9652").isRight shouldBe true
    parseAsIPv6("2001:12:0:0:0:0:3257").isRight shouldBe false
    parseAsIPv6("2001:cdba::3257:9652").isRight shouldBe true
    parseAsIPv6("2001:cdba::3257:9652::").isRight shouldBe false
    parseAsIPv6("::0.0.12.1").isRight shouldBe true
    parseAsIPv6("::0.0.12.310").isRight shouldBe false
    parseAsIPv6("2001:abdba::3257:9652").isRight shouldBe false

    parse("2001:xfg::3257:9652").isRight shouldBe false
    parse("0.0.0.0").isRight shouldBe true
    parse("2001:cdba::3257:9652").isRight shouldBe true
    parse("0.0").isRight shouldBe false

    ipv4("192.0.1.1").groups shouldBe List(192, 0, 1, 1)
    ipv6("::10.0.1.1").groups shouldBe List(0, 0, 0, 0, 0, 0, 0xA00, 0x101)
    InetAddress("1ABB::1A").groups shouldBe List(0x1ABB, 0, 0, 0, 0, 0, 0, 0x1A)
  }
  test("domain name") {
    import DomainValidator._

    isValid("ff.aa.com") shouldBe true
    isValid("1a-f.xft") shouldBe true
    isValid("1a-f.1xft") shouldBe false
    isValid("1a_f.xft") shouldBe false
    isValid("a-" * 30 + "a.com") shouldBe true
    isValid("a-" * 40 + "a.com") shouldBe false
    isValid("localhost", allowLocal = true) shouldBe true
    isValid("localhost") shouldBe false
    isValid(null) shouldBe false
  }
  test("email") {
    import Email._

    parse("jj@ff.aa.com").isRight shouldBe true
    parse("j.j@1a-f.xft").isRight shouldBe true
    parse("jj@1a-f.1xft").isRight shouldBe false
    parse("disposable.style.email.with+symbol@example.com").isRight shouldBe true
    parse("other.email-with-hyphen@example.com").isRight shouldBe true
    parse("other.email-with-hyphen@example.com").isRight shouldBe true
    parse("user.name+tag+sorting@example.com").isRight shouldBe true
    parse("x@example.com").isRight shouldBe true
    parse("\"very.().isRight,:;<>[]\\\".VERY.\\\"very@\\\\ \\\"very\\\".unusual\"@strange.example.com").isRight shouldBe true
    parse("example-indeed@strange-example.com").isRight shouldBe true
    parse("#!$%&'*+-/=?^_`{}|~@example.org").isRight shouldBe true
    parse("\"().isRight<>[]:,;@\\\\\\\"!#$%&'-/=?^_`{}| ~.a\"@example.org").isRight shouldBe true
    parse("user@[2001:DB8::1]").isRight shouldBe true
    parse("\" \"@example.org").isRight shouldBe true

    parse("A@b@c@example.com").isRight shouldBe false
    parse("a\"b(c).isRightd,e:f;g<h>i[j\\k]l@example.com").isRight shouldBe false
    parse("just\"not\"right@example.com").isRight shouldBe false
    parse("this is\"not\\allowed@example.com").isRight shouldBe false
    parse("this\\ still-not-allowed@example.com").isRight shouldBe false
    parse("this-still\\\"not-allowed@example.com").isRight shouldBe false
    parse("this-still-not\\\\allowed@example.com").isRight shouldBe false
    parse("this-is-allowed@example.com").isRight shouldBe true
    parse("1234567890123456789012345678901234567890123456789012345678901234+x@example.com").isRight shouldBe false
    parse("john..doe@example.com").isRight shouldBe false
    parse("john.doe").isRight shouldBe false
    parse("john.").isRight shouldBe false
    parse(null).isRight shouldBe false

    the[ParseException] thrownBy {
      Email("john..doe@example.com")
    } should have message "Format Error (Invalid User) in 'john..doe'"

    a[ParseException] should be thrownBy Email("John..doe", "example.com")

    Email("jj@example.com") match {
      case Email(user, domain) =>
        user shouldBe "jj"
        domain shouldBe "example.com"
      case _ =>
        fail("Should match")
    }

    "jj@example.com" match {
      case Email(e) =>
        e.user shouldBe "jj"
        e.domain shouldBe "example.com"
      case _ =>
        fail("Should match")
    }

  }

  test("uri") {
    import UriValidator._

    isUri("12 34") shouldBe false
    isUri("ab6") shouldBe false
    isUri("//example.com") shouldBe true
    isUri("//example.com", allowRelative = false) shouldBe false
    isUri(null) shouldBe false
    isUri("http://example.org:8080/example.html", allowRelative = false) shouldBe true
  }
  test("uri template") {
    import UriTemplate._
    def s(s: String) = Literal(s)
    def e(e: String) = Expression(e)

    parse("aaxbb").right.get.parts shouldBe Array(s("aaxbb"))
    parse("aa}xbb").right.get.parts shouldBe Array(s("aa}xbb"))
    parse("aa{xx}bb").right.get.parts shouldBe Array(s("aa"), e("xx"), s("bb"))
    parse("aa{xx}{yy}bb{xx}").right.get.parts shouldBe Array(s("aa"), e("xx"), e("yy"), s("bb"), e("xx"))
    parse("aa{.xx}bb").right.get.parts shouldBe Array(s("aa"), e(".xx"), s("bb"))
    parse("aa{.xx:10,b1_*,c4}bb").right.get.parts shouldBe Array(s("aa"), e(".xx:10,b1_*,c4"), s("bb"))

    parse("aa{xbb").left.get.message shouldBe "Format Error (Unclosed braces) in 'aa{xbb'"
    parse("aa{yy}aa{xbb").left.get.message shouldBe "Format Error (Unclosed braces) in 'aa{yy}aa{xbb'"
    parse("aa{>xx}bb").left.get.message shouldBe "Format Error (Illegal operator) in '>xx'"
    parse("aa{#xx:1000}bb").left.get.message shouldBe "Format Error (Illegal Variable Specification) in 'xx:1000'"
    parse("aa{#xx:10,y1#}bb").left.get.message shouldBe "Format Error (Illegal Variable Specification) in 'y1#'"
    parse("aa{}bb").left.get.message shouldBe "Format Error (Empty expression) in ''"

    UriTemplate("aaxbb").parts shouldBe Array(s("aaxbb"))

    Operator("+").equals(null) shouldBe false

    Operator("+").toString shouldBe "+"
    Operator.empty.toString shouldBe ""
  }
}
