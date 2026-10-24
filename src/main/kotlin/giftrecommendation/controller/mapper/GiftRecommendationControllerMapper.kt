package org.podborochka.giftrecommendation.controller.mapper

import org.podborochka.giftrecommendation.application.model.CreateGiftRecommendationCommand
import org.podborochka.giftrecommendation.application.model.GiftRecommendationModel
import org.podborochka.giftrecommendation.application.model.UpdateGiftRecommendationCommand
import org.podborochka.giftrecommendation.controller.dto.CreateGiftRecommendationRequest
import org.podborochka.giftrecommendation.controller.dto.GiftRecommendationResponse
import org.podborochka.giftrecommendation.controller.dto.UpdateGiftRecommendationRequest
import org.springframework.stereotype.Component

@Component
class GiftRecommendationControllerMapper {

    fun toCreateCommand(
        userId: Long,
        searchRequestId: Long,
        request: CreateGiftRecommendationRequest
    ): CreateGiftRecommendationCommand =
        CreateGiftRecommendationCommand(
            userId = userId,
            searchRequestId = searchRequestId,
            productId = request.productId,
            title = request.title,
            description = request.description,
            reasoning = request.reasoning,
            approximatePrice = request.approximatePrice,
            searchQuery = request.searchQuery,
            ozonSearchUrl = request.ozonSearchUrl,
            wildberriesSearchUrl = request.wildberriesSearchUrl,
            yandexMarketSearchUrl = request.yandexMarketSearchUrl,
            rankPosition = request.rankPosition
        )

    fun toUpdateCommand(request: UpdateGiftRecommendationRequest): UpdateGiftRecommendationCommand =
        UpdateGiftRecommendationCommand(
            productId = request.productId,
            title = request.title,
            description = request.description,
            reasoning = request.reasoning,
            approximatePrice = request.approximatePrice,
            searchQuery = request.searchQuery,
            ozonSearchUrl = request.ozonSearchUrl,
            wildberriesSearchUrl = request.wildberriesSearchUrl,
            yandexMarketSearchUrl = request.yandexMarketSearchUrl,
            rankPosition = request.rankPosition
        )

    fun toResponse(model: GiftRecommendationModel): GiftRecommendationResponse =
        GiftRecommendationResponse(
            id = model.id,
            searchRequestId = model.searchRequestId,
            productId = model.productId,
            title = model.title,
            description = model.description,
            reasoning = model.reasoning,
            approximatePrice = model.approximatePrice,
            searchQuery = model.searchQuery,
            ozonSearchUrl = model.ozonSearchUrl,
            wildberriesSearchUrl = model.wildberriesSearchUrl,
            yandexMarketSearchUrl = model.yandexMarketSearchUrl,
            rankPosition = model.rankPosition,
            createdAt = model.createdAt.toString()
        )
}
