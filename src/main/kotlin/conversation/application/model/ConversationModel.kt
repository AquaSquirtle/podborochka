package org.podborochka.conversation.application.model

import java.time.LocalDateTime

data class ConversationModel(
    val id: Long,
    val userId: Long,
    val title: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)