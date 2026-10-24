package org.podborochka.message.application.model

import org.podborochka.message.domain.MessageRole

data class CreateMessageCommand(
    val userId: Long,
    val conversationId: Long,
    val role: MessageRole,
    val content: String
)