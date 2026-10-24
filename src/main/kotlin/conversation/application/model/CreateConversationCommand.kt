package org.podborochka.conversation.application.model

data class CreateConversationCommand(
    val userId: Long,
    val title: String?
)