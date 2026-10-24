package org.podborochka.giftrecommendation.controller.dto

import jakarta.validation.constraints.Min

data class UpdateGiftRecommendationRequest(
    val productId: Long? = null,
    val title: String? = null,
    val description: String? = null,
    val reasoning: String? = null,
    val approximatePrice: Int? = null,
    val searchQuery: String? = null,
    val ozonSearchUrl: String? = null,
    val wildberriesSearchUrl: String? = null,
    val yandexMarketSearchUrl: String? = null,

    @Min(1)
    val rankPosition: Int? = null
)
