package org.mulesoft.common.logger
import scala.scalajs.js

@js.native
trait ClientLogger {
  def error(message: String): Unit

  def warn(message: String): Unit

  def info(message: String): Unit

  def log(message: String): Unit
}

case class ClientLoggerAdapter(clientLogger: ClientLogger) extends AbstractLogger {
  override protected def executeLogging(msg: String, severity: MessageSeverity.MessageSeverity): Unit =
    severity match {
      case MessageSeverity.ERROR   => clientLogger.error(msg)
      case MessageSeverity.WARNING => clientLogger.warn(msg)
      case MessageSeverity.DEBUG   => clientLogger.log(msg)
    }

  override protected val settings: Option[LoggerSettings] = None

  override def withSettings(settings: LoggerSettings): ClientLoggerAdapter.this.type = this

}
