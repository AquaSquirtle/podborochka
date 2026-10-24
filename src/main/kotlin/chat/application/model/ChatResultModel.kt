package org.podborochka.chat.application.model

data class ChatResultModel(
    val conversationId: Long,
    val prompt: String,
    val assistantMessage: String,
    val recipientSummary: String?,
    val recommendations: List<ChatRecommendationItemModel>
)