package org.podborochka.chat.infrastructure.llm.dto

data class GiftIntentLlmResponse(
    val recipientDescription: String?,
    val occasion: String?,
    val budgetMin: Int?,
    val budgetMax: Int?,
    val interests: List<String> = emptyList(),
    val constraints: List<String> = emptyList(),
    val searchQueries: List<String> = emptyList(),
)