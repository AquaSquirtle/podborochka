package org.podborochka.chat.application.model

data class ChatRecommendationItemModel(
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