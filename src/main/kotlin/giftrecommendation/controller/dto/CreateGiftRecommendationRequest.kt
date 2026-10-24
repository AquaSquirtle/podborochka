package org.podborochka.giftrecommendation.controller.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class CreateGiftRecommendationRequest(

    val productId: Long? = null,

    @NotBlank
    val title: String,

    val description: String? = null,

    val reasoning: String? = null,

    val approximatePrice: Int? = null,

    val searchQuery: String? = null,

    val ozonSearchUrl: String? = null,

    val wildberriesSearchUrl: String? = null,

    val yandexMarketSearchUrl: String? = null,

    @Min(1)
    val rankPosition: Int
)
