package org.mulesoft.common.time

import org.mulesoft.common.parse._
import org.mulesoft.common.time.SimpleDateTime._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.OptionValues

/**
  * Check SimpleDateTime
  */
trait SimpleDateTimeTest extends AnyFunSuite with Matchers with OptionValues {
  test("singletons") {
    Epoch.day shouldBe 1
    Epoch.month shouldBe 1
    Epoch.year shouldBe 1970
    Epoch.timeOfDay shouldBe Some(ZeroTime)
    ZeroTime.hour shouldBe 0
    ZeroTime.minute shouldBe 0
    ZeroTime.second shouldBe 0
    ZeroTime.nano shouldBe 0
  }
  test("parse date-time") {
    "1970-1-1 0:0:0Z" match {
      case SimpleDateTime(s) =>
        s shouldBe Epoch
        s.toString shouldBe "1970-01-01T00:00:00Z"
    }
    "1970-01-01T00:00:00.0000+00" match {
      case SimpleDateTime(s) =>
        s shouldBe Epoch
        s.toString shouldBe "1970-01-01T00:00:00Z"
    }
    "2010-10-31  13:40-03:30" match {
      case SimpleDateTime(s) =>
        s shouldBe SimpleDateTime(2010, 10, 31, TimeOfDay(13, 40), -210)
        s.toString shouldBe "2010-10-31T13:40:00-03:30"
    }
    "2010-10-31" match {
      case SimpleDateTime(s) =>
        s shouldBe SimpleDateTime(2010, 10, 31)
        s.toString shouldBe "2010-10-31"
    }
    "2010-10-31 13:00" match {
      case SimpleDateTime(s) =>
        s shouldBe SimpleDateTime(2010, 10, 31, TimeOfDay(13))
        s.toString shouldBe "2010-10-31T13:00:00"
    }
    "2010-10-31 13:00Z" match {
      case SimpleDateTime(s) =>
        s shouldBe SimpleDateTime(2010, 10, 31, TimeOfDay(13), 0)
        s.toString shouldBe "2010-10-31T13:00:00Z"
    }
    "12010-10-31 13:00Z" match {
      case SimpleDateTime(_) => fail("Should not match")
      case _                 =>
    }
    "2015-02-28T11:00:00.123456789Z" match {
      case SimpleDateTime(s) =>
        s shouldBe SimpleDateTime(2015, 2, 28, TimeOfDay(11, 0, 0, 123456789), 0)
        s.toString shouldBe "2015-02-28T11:00:00.123Z"
    }
    "2015-02-28T11:00:00.1Z" match {
      case SimpleDateTime(s) =>
        s shouldBe SimpleDateTime(2015, 2, 28, TimeOfDay(11, 0, 0, 100000000), 0)
        s.toString shouldBe "2015-02-28T11:00:00.1Z"
    }
    "2019-01-07T11:00:00.05" match {
      case SimpleDateTime(s) =>
        s shouldBe SimpleDateTime(2019, 1, 7, TimeOfDay(11, 0, 0, 50 *1000 * 1000))
        s.toString shouldBe "2019-01-07T11:00:00.05"
    }
    parse("2015-02-28T11:00:00.1234567890Z").left.toOption.get shouldBe FormatError("2015-02-28T11:00:00.1234567890Z")

    parseFullTime("xxx").isLeft shouldBe true
  }

  test("parse date") {
    parseAsDate("1970-1-1") shouldBe SimpleDateTime(1970, 1, 1)
    parseAsDate("2010-10-31") shouldBe SimpleDateTime(2010, 10, 31)
    parseAsDate("2000-02-29") shouldBe SimpleDateTime(2000, 2, 29)
    parseAsDate("1500-02-29") shouldBe SimpleDateTime(1500, 2, 29)
    parseAsDate("2344-02-29") shouldBe SimpleDateTime(2344, 2, 29)

    parseDate("2015-22-28").left.toOption.get shouldBe RangeError(22)
    parseDate("2015-12-32").left.toOption.get shouldBe RangeError(32)
    parseDate("0000-12-32").left.toOption.get shouldBe RangeError(0)
    parseDate("2015-11-31").left.toOption.get shouldBe RangeError(31)
    parseDate("2015-02-30").left.toOption.get shouldBe RangeError(30)
    parseDate("2015-02-29").left.toOption.get shouldBe RangeError(29)
    parseDate("1900-02-29").left.toOption.get shouldBe RangeError(29)

    parseDate("2015-02-28T11:00:00").left.toOption.get shouldBe FormatError("2015-02-28T11:00:00")
    parseDate("2015/02/28").left.toOption.get.message shouldBe "Format Error in '2015/02/28'"
  }

  test("toString date") {
    parseAsDate("1970-1-1").toString shouldBe "1970-01-01"
    parseAsDate("2010-10-31").toString shouldBe "2010-10-31"
  }

    test("parse partial time") {
    parsePartialTime("0:0").toOption.value shouldBe TimeOfDay(0)
    parsePartialTime("33:0").left.toOption.get shouldBe RangeError(33)
    parsePartialTime("14:61").left.toOption.get shouldBe RangeError(61)
    parsePartialTime("24:01").left.toOption.get.message shouldBe "Value '1' out of range"
    parsePartialTime("24:00:02").left.toOption.get shouldBe RangeError(2)
    parsePartialTime("24:00:00.0001").left.toOption.get shouldBe RangeError(100000)
    parsePartialTime("xxx").isLeft shouldBe true
  }
  test("parse full time") {
    parseFullTime("0:0-03:30").toOption.value shouldBe (TimeOfDay(0), Some(-210 * 60))
    parseFullTime("0:0-33:30").left.toOption.get shouldBe RangeError(-33)
    parseFullTime("0:0-03:90").left.toOption.get shouldBe RangeError(90)
  }

  test("toString time") {
    parsePartialTime("0:0").toOption.value.toString shouldBe "00:00:00"
    parsePartialTime("10:30:01.1237").toOption.value.toString shouldBe "10:30:01.124"
  }

  private def parseAsDate(date: String): SimpleDateTime =  parseDate(date).toOption.value
}
