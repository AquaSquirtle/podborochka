package org.podborochka.chat.controller.dto

data class SendChatMessageResponse(
    val conversationId: Long,
    val prompt: String,
    val assistantMessage: String,
    val recipientSummary: String?,
    val recommendations: List<ChatRecommendationResponse>
)