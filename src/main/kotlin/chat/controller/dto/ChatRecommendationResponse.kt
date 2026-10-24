package org.podborochka.chat.controller.dto

data class ChatRecommendationResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val reasoning: String?,
    val approximatePrice: Int?,
    val searchQuery: String?,
    val rankPosition: Int,
    val ozonSearchUrl: String?,
    val wildberriesSearchUrl: String?,
    val yandexMarketSearchUrl: String?
)