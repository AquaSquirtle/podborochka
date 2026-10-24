package org.podborochka.chat.application.model

data class ProcessChatMessageCommand(
    val userId: Long,
    val conversationId: Long,
    val prompt: String
)