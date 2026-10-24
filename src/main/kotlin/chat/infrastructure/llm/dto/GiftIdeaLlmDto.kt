package org.podborochka.chat.infrastructure.llm.dto

data class GiftIdeaLlmDto(
    val title: String,
    val description: String,
    val reasoning: String,
    val approximatePrice: Int?,
    val searchQuery: String,
    val rankPosition: Int
)