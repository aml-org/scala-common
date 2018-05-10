package org.mulesoft.common

import java.lang.Character._

package object core {

  /**
    * Common utility methods to deal with Strings.
    */
  implicit class Strings(val str: String) extends AnyVal {

    /** If the String is not null returns the String, else returns "". */
    def notNull: String = if (str == null) "" else str

    /** Returns the number of occurrences of a given char into an String. */
    def count(c: Char): Int = {
      if (str == null) return 0
      var result = 0
      for (i <- 0 until str.length)
        if (str.charAt(i) == c) result += 1
      result
    }

    def hex(ch: Int): String = Integer.toHexString(ch).toUpperCase


    // Adapted from Apache Commons
    // https://commons.apache.org/proper/commons-lang/javadocs/api-2.6/src-html/org/apache/commons/lang/StringEscapeUtils.html#line.158
    def escapeJavaStyleString(str: String): String = {

      if (str == null) {
        str
      } else {
        val out = new StringBuilder(2 * str.length)
        for {
          i <- Range(0, str.length)
        } {
          val ch = str.charAt(i)

          if (ch > 0xfff) out ++= ("\\u" + hex(ch))
          else if (ch > 0xff) out ++= ("\\u0" + hex(ch))
          else if (ch > 0x7f) out ++= ("\\u00" + hex(ch))
          else if (ch < 32) ch match {
            case '\b' =>
              out += '\\'
              out += 'b'
            case '\n' =>
              out += '\\'
              out += 'n'
            case '\t' =>
              out += '\\'
              out += 't'
            case '\f' =>
              out += '\\'
              out += 'f'
            case '\r' =>
              out += '\\'
              out += 'r'
            case _ =>
              if (ch == 0) out ++= "\\0"
              else if (ch > 0xf) out ++=("\\u00" + hex(ch))
              else out ++= ("\\u000" + hex(ch))
          }
          else ch match {
            // case '\'' =>
            //  out += '\\'
            //  out += '\''
            case '"' =>
              out += '\\'
              out += '"'
            case '\\' =>
              out += '\\'
              out += '\\'
            // case '/' =>
            //  out += '\\'
            //  out += '/'
            case _ =>
              out += ch
          }
        }
        out.mkString
      }
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


    def encode: String = escapeJavaStyleString(str)


    /** Compare two Strings ignoring the spaces in each */
    def equalsIgnoreSpaces(str2: String): Boolean = {
      def charAt(s: String, i: Int) = if (i >= s.length) '\u0000' else s.charAt(i)

      var i = 0
      var j = 0
      while (i < str.length || j < str2.length) {
        val c1 = charAt(str, i)
        if (c1.isWhitespace) i = i + 1
        else {
          val c2 = charAt(str2, j)
          if (c2.isWhitespace) j = j + 1
          else {
            if (c1 != c2) return false
            i = i + 1
            j = j + 1
          }
        }
      }
      true
    }

    /** Interpreting the string as a file name replace The extension */
    def replaceExtension(newExt: String): String = {
        val lastDot = str.lastIndexOf('.')
        val ext = if (newExt == null || newExt.isEmpty) "" else if (newExt(0) != '.') '.' + newExt else newExt
        if (lastDot == -1) str + ext else str.substring(0, lastDot) + ext
    }

    /** Add quotes to the string. If the string already has quotes, returns the same string */
    def quoted: String = {
      if (str.startsWith("\"") && str.endsWith("\"")) str
      else s""""$str""""
    }
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
}
