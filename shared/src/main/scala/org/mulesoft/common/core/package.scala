package org.mulesoft.common

import java.lang.Character._
import java.net.IDN

package object core {

  /**
    * [3]	c-byte-order-mark	::=	#xFEFF
    */
  val BomMark = 0xFEFF
  /**
    * Common utility methods to deal with Strings.
    */
  implicit class Strings(val str: String) extends AnyVal {

    /** If the String is not null returns the String, else returns "". */
    def notNull: String = if (str == null) "" else str

    /** Check if the String is null or empty */
    def isNullOrEmpty: Boolean = str == null || str.isEmpty

    /** Check if the String is not null and non empty */
    def nonNullNorEmpty: Boolean = str != null && !str.isEmpty

    /** Returns the number of occurrences of a given char into an String. */
    def count(c: Char): Int = {
      if (str == null) return 0
      var result = 0
      for (i <- 0 until str.length)
        if (str.charAt(i) == c) result += 1
      result
    }

    /** Parse a String with escape sequences. */
    def decode: String = decode(false)

    /** Parse a String with escape sequences. Ignore encoding errors */
    def decode(ignoreErrors: Boolean): String = {

      if (str == null) return str
      val length = str.length

      if (length == 0) return str
      val buffer = new StringBuilder(length)

      var i = 0
      while (i < length) {
        val chr = str.charAt(i)
        i += 1
        if (chr != '\\' || i >= length) buffer.append(chr)
        else {
          val chr = str.charAt(i)
          i += 1
          buffer.append(chr match {
            case 'U' =>
              i += 8
              decodeUnicodeChar(str, i - 8, i, ignoreErrors)
            case 'u' =>
              i += 4
              decodeUnicodeChar(str, i - 4, i, ignoreErrors)
            case 'x' =>
              i += 2
              decodeUnicodeChar(str, i - 2, i, ignoreErrors)
            case 't' => "\t"
            case 'r' => "\r"
            case 'n' => "\n"
            case 'f' => "\f"
            case 'a' => '\u0007'
            case 'b' => "\b"
            case 'v' => "\u000B"
            case 'e' => '\u001B'
            case '0' => '\u0000'
            case 'N' => "\u0085"
            case '_' => "\u00A0"
            case 'L' => "\u2028"
            case 'P' => "\u2029"
            case _   => chr.toString
          })
        }
      }
      buffer.toString
    }

    def encode: String = {
      encode(encodeNonAscii = true)
    }

    def encode(encodeNonAscii: Boolean) = {
      var f = firstEscaped
      if (f == -1) str
      else {
        val out = new StringBuilder(2 * str.length)
        out ++= str.substring(0, f)
        while (f < str.length) {
          val ch = str.charAt(f)
          if (ch < 32) {
            out += '\\'
            ch match {
              case '\b'                => out += 'b'
              case '\n'                => out += 'n'
              case '\t'                => out += 't'
              case '\f'                => out += 'f'
              case '\r'                => out += 'r'
              case _                   => out ++= "u00" + (if (ch > 0xf) "" else "0") + ch.toHexString
            }
          }
          else if (ch < 0x7F) {
            if (ch == '"' || ch == '\\') out += '\\'
            out += ch
          }
          else if (encodeNonAscii) {
            out ++= "\\u"
            if (ch <= 0xfff) {
              if (ch > 0xff) out += '0' else out ++= "00"
            }
            out ++= ch.toHexString
          }
          else {
            out += ch
          }
          f += 1
        }
        out.toString
      }
    }

    private def firstEscaped: Int =
      if (str == null) -1
      else {
        var i      = 0
        val length = str.length
        while (i < length) {
          if (str.charAt(i).needsToBeEscaped) return i
          i += 1
        }
        -1
      }

    /** Compare two Strings ignoring the spaces in each */
    def equalsIgnoreSpaces(str2: String): Boolean = {
      def charAt(s: String, i: Int) = if (i >= s.length) '\u0000' else s.charAt(i)

      var i = 0
      var j = 0
      while (i < str.length || j < str2.length) {
        val c1 = charAt(str, i)
        if (c1.isWhitespace || c1.isBom) i = i + 1
        else {
          val c2 = charAt(str2, j)
          if (c2.isWhitespace || c2.isBom) j = j + 1
          else {
            if (c1 != c2) return false
            i = i + 1
            j = j + 1
          }
        }
      }
      true
    }

    def stripSpaces: String =
      if (str.isNullOrEmpty) str
      else if (!str.contains(' ')) str
      else {
        val len    = str.length
        val result = new StringBuilder(len)
        var i      = 0
        while (i < len) {
          val c = str.charAt(i)
          if (c != ' ') result.append(c)
          i += 1
        }
        result.result()
      }

    /** Interpreting the string as a file name replace The extension */
    def replaceExtension(newExt: String): String = {
      val lastDot = str.lastIndexOf('.')
      val ext     = if (newExt.isNullOrEmpty) "" else if (newExt(0) != '.') '.'.toString + newExt else newExt
      if (lastDot == -1) str + ext else str.substring(0, lastDot) + ext
    }

    /** Add quotes to the string. If the string already has quotes, returns the same string */
    def quoted: String = if (str.startsWith("\"") && str.endsWith("\"")) str else '"'.toString + str + '"'

    /** Returns true if all characters are ascii */
    def isOnlyAscii: Boolean = str.isNullOrEmpty || str.forall(_.isAscii)

    def toPunnycode: String = if (isOnlyAscii) str else IDN.toASCII(str)
  }

  private def decodeUnicodeChar(str: String, from: Int, to: Int, ignoreErrors: Boolean): String = {
    var value = 0
    for (i <- from until to) {
      val n = if (i < str.length) digit(str.charAt(i), 16) else -1
      if (n == -1) {
        if (ignoreErrors) return str.substring(i, Math.min(to, str.length))
        throw new IllegalArgumentException("Malformed unicode encoding: " + str)
      }
      value = (value << 4) | n
    }
    new String(toChars(value))
  }

  /** Count the number of times a given predicate holds true The predicate receives an Int as a parameter */
  def countWhile(predicate: Int => Boolean): Int = {
    var i = 0
    while (predicate(i)) i = i + 1
    i
  }

  /** Common utilities to deal with Characters */
  implicit class Chars(val chr: Char) extends AnyVal {

    /** Return an String with n repetitions of the current char */
    def repeat(n: Int): String =
      if (n == 0) ""
      else {
        val buf = new Array[Char](n)
        var i   = 0
        while (i < n) {
          buf(i) = chr
          i += 1
        }
        new String(buf)
      }

    /** Convert to an Hexadecimal String (In Uppercase) */
    def toHexString: String = Integer.toHexString(chr).toUpperCase

    /** It is an Hexadecimal Digit */
    def isHexDigit: Boolean = chr.isDigit || chr >= 'A' && chr <= 'F' || chr >= 'a' && chr <= 'f'

    @inline def needsToBeEscaped: Boolean = chr < ' ' || chr >= 0x7F || chr == '\\' || chr == '"'
    @inline def isAscii: Boolean          = chr <= 0x7F
    @inline def isBom: Boolean = chr == BomMark

  }
}
