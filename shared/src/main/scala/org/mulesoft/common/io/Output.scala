package org.mulesoft.common.io

import java.io.Writer
import scala.language.implicitConversions
import scala.scalajs.js.annotation.{JSExport, JSExportAll, JSExportTopLevel}

/**
  * An Output is a type class for defining classes to output to like java.io.Writer or java.io.OutputStream
  *
  */
trait Output[W] {
  def append(w: W, s: CharSequence): Unit
  def append(w: W, c: Char): Unit = append(w, c.toString)

  def write(w: W, cbuf: Array[Char], off: Int, len: Int): Unit = append(w, new String(cbuf, off, len))
  def write(w: W, cbuf: Array[Char]): Unit                     = write(w, cbuf, 0, cbuf.length)

  def flush(w: W): Unit = {}
  def close(w: W): Unit
}

object Output {

  /** OutputOps implements operations on W:Output mostly by delegation to the Output[W] trait */
  implicit class OutputOps[W](val w: W) {

    def append(s: CharSequence)(implicit o: Output[W]): Unit = o.append(w, s)
    def append(c: Char)(implicit o: Output[W]): Unit         = o.append(w, c)

    def write(cbuf: Array[Char], off: Int, len: Int)(implicit o: Output[W]): Unit = o.write(w, cbuf, off, len)
    def write(cbuf: Array[Char])(implicit o: Output[W]): Unit                     = o.write(w, cbuf)

    def close(implicit o: Output[W]): Unit = o.close(w)
    def flush(implicit o: Output[W]): Unit = o.flush(w)
  }

  implicit object OutputWriter extends Output[Writer] {

    override def append(writer: Writer, string: CharSequence): Unit = writer.append(string)
    override def append(writer: Writer, chr: Char): Unit            = writer.append(chr)

    override def write(w: Writer, cbuf: Array[Char], off: Int, len: Int): Unit = w.write(cbuf, off, len)
    override def write(w: Writer, cbuf: Array[Char]): Unit                     = w.write(cbuf)

    override def flush(writer: Writer): Unit = writer.flush()
    override def close(writer: Writer): Unit = writer.close()
  }

  implicit def outputWriter[W <: Writer]: Output[W] = OutputWriter.asInstanceOf[Output[W]]

  implicit object StringBufferWriter extends Output[LimitedStringBuffer] {

    override def append(w: LimitedStringBuffer, s: CharSequence): Unit = w.append(s)

    override def close(w: LimitedStringBuffer): Unit = ()
  }

  implicit def stringBufferWriter[W <: LimitedStringBuffer]: Output[W] = StringBufferWriter.asInstanceOf[Output[W]]
}

@JSExportTopLevel("LimitedStringBuffer")
case class LimitedStringBuffer(limit: Int) {

  private val buf: StringBuffer = new StringBuffer()

  @JSExport
  def length: Int = buf.length()

  @JSExport
  override def toString: String = buf.toString

  def append(s: CharSequence): this.type = {
    if(s.length + length > limit) throw LimitReachedException()
    buf.append(s)
    this
  }
}

@JSExportAll
@JSExportTopLevel("LimitReachedException")
case class LimitReachedException() extends Exception()
