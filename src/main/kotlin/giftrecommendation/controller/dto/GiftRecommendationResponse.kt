package org.podborochka.giftrecommendation.controller.dto

data class GiftRecommendationResponse(
    val id: Long,
    val searchRequestId: Long,
    val productId: Long?,
    val title: String,
    val description: String?,
    val reasoning: String?,
    val approximatePrice: Int?,
    val searchQuery: String?,
    val ozonSearchUrl: String?,
    val wildberriesSearchUrl: String?,
    val yandexMarketSearchUrl: String?,
    val rankPosition: Int,
    val createdAt: String
)
