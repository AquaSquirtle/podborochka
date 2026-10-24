package org.podborochka.giftrecommendation.infrastructure.mapper

import org.podborochka.giftrecommendation.application.model.GiftRecommendationModel
import org.podborochka.giftrecommendation.infrastructure.entity.GiftRecommendationEntity
import org.podborochka.giftsearchrequest.infrastructure.entity.GiftSearchRequestEntity
import org.podborochka.product.infrastructure.entity.ProductEntity
import org.springframework.stereotype.Component

@Component
class GiftRecommendationPersistenceMapper {

    fun toModel(entity: GiftRecommendationEntity): GiftRecommendationModel =
        GiftRecommendationModel(
            id = requireNotNull(entity.id),
            searchRequestId = requireNotNull(entity.searchRequest.id),
            productId = entity.product?.id,
            title = entity.title,
            description = entity.description,
            reasoning = entity.reasoning,
            approximatePrice = entity.approximatePrice,
            searchQuery = entity.searchQuery,
            ozonSearchUrl = entity.ozonSearchUrl,
            wildberriesSearchUrl = entity.wildberriesSearchUrl,
            yandexMarketSearchUrl = entity.yandexMarketSearchUrl,
            rankPosition = entity.rankPosition,
            createdAt = requireNotNull(entity.createdAt)
        )

    fun toNewEntity(
        searchRequest: GiftSearchRequestEntity,
        product: ProductEntity?,
        title: String,
        description: String?,
        reasoning: String?,
        approximatePrice: Int?,
        searchQuery: String?,
        ozonSearchUrl: String?,
        wildberriesSearchUrl: String?,
        yandexMarketSearchUrl: String?,
        rankPosition: Int
    ): GiftRecommendationEntity =
        GiftRecommendationEntity(
            searchRequest = searchRequest,
            product = product,
            title = title,
            description = description,
            reasoning = reasoning,
            approximatePrice = approximatePrice,
            searchQuery = searchQuery,
            ozonSearchUrl = ozonSearchUrl,
            wildberriesSearchUrl = wildberriesSearchUrl,
            yandexMarketSearchUrl = yandexMarketSearchUrl,
            rankPosition = rankPosition
        )
}
