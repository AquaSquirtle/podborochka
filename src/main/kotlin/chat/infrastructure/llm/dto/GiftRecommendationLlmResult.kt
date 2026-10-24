package org.podborochka.chat.infrastructure.llm.dto

data class GiftRecommendationLlmResult(
    val recipientSummary: String?,
    val recommendations: List<GiftIdeaLlmDto>
)