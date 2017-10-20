package org.yaml.render

import org.mulesoft.common.core.Strings
import org.yaml.model.YType._
import org.yaml.model._

/**
  * Yaml Render
  */
class JsonRender private () {
  private val builder           = new StringBuilder
  override def toString: String = builder.toString

  private var indentation    = 0
  private def indent(): Unit = indentation += 2
  private def dedent(): Unit = indentation -= 2
  private def renderIndent(): JsonRender = {
    for (_ <- 0 until indentation) builder append ' '
    this
  }
  private def render(node: YNode): JsonRender = {
    node.value match {
      case m: YMap      => renderMap(m)
      case s: YSequence => renderSeq(s)
      case s: YScalar   => renderScalar(node.tagType, s)

    }
    this
  }
  private def renderSeq(seq: YSequence) =
    if (seq.isEmpty) render("[]")
    else {
      render("[\n")
      indent()
      seq.nodes foreach {
        renderIndent().render(_).render(",\n")
      }
      chopLastComma()
      dedent()
      renderIndent().render("]")
    }

  private def chopLastComma() = builder.deleteCharAt(builder.length - 2)

  private def renderMap(map: YMap) =
    if (map.isEmpty) render("{}")
    else {
      render("{\n")
      indent()
      map.entries foreach { e =>
        renderIndent().render(e.key).render(": ").render(e.value).render(",\n")
      }
      chopLastComma()
      dedent()
      renderIndent().render("}")
    }

  private def renderScalar(t: YType, scalar: YScalar): Unit =
    t match {
      case Int | Float | Bool => render(scalar.value.toString)
      case Null               => render("null")
      case _                  => render('"' + scalar.text.encode + '"')
    }

  private def render(value: String) = {
    builder.append(value)
    this
  }
}

object JsonRender {

  /** Render a Seq of Parts as an String */
  def render(doc: YDocument): String = {
    val builder = new JsonRender()
    builder.render(doc.node).render("\n")
    builder.toString
  }
}