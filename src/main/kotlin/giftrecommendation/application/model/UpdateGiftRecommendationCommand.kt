package org.podborochka.giftrecommendation.application.model

data class UpdateGiftRecommendationCommand(
    val productId: Long? = null,
    val title: String? = null,
    val description: String? = null,
    val reasoning: String? = null,
    val approximatePrice: Int? = null,
    val searchQuery: String? = null,
    val ozonSearchUrl: String? = null,
    val wildberriesSearchUrl: String? = null,
    val yandexMarketSearchUrl: String? = null,
    val rankPosition: Int? = null
)
