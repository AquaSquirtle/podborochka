package org.podborochka.message.application.model

import org.podborochka.message.domain.MessageRole
import java.time.LocalDateTime

data class MessageModel(
    val id: Long,
    val conversationId: Long,
    val role: MessageRole,
    val content: String,
    val createdAt: LocalDateTime
)