package org.podborochka.conversation.controller.dto

data class ConversationResponse(
    val id: Long,
    val title: String?,
    val createdAt: String,
    val updatedAt: String
)