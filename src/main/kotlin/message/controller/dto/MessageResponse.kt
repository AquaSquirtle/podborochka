package org.podborochka.message.controller.dto

data class MessageResponse(
    val id: Long,
    val conversationId: Long,
    val role: String,
    val content: String,
    val createdAt: String
)