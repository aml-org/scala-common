package org.mulesoft.common.logger

import org.mulesoft.common.logger.MessageSeverity.MessageSeverity

case class LogMessage(content: String, severity: MessageSeverity, component: String, subComponent: String)
