package org.podborochka.wishlist.infrastructure.mapper

import org.podborochka.product.infrastructure.entity.ProductEntity
import org.podborochka.giftrecommendation.infrastructure.entity.GiftRecommendationEntity
import org.podborochka.user.infrastructure.entity.UserEntity
import org.podborochka.wishlist.application.model.WishlistItemModel
import org.podborochka.wishlist.infrastructure.entity.WishlistItemEntity
import org.springframework.stereotype.Component

@Component
class WishlistPersistenceMapper {

    fun toModel(entity: WishlistItemEntity): WishlistItemModel =
        WishlistItemModel(
            id = requireNotNull(entity.id),
            userId = requireNotNull(entity.user.id),
            productId = entity.product?.id,
            recommendationId = entity.recommendation?.id,
            customTitle = entity.customTitle,
            customUrl = entity.customUrl,
            notes = entity.notes,
            isPurchased = entity.isPurchased,
            createdAt = requireNotNull(entity.createdAt),
            updatedAt = requireNotNull(entity.updatedAt)
        )

    fun toNewEntity(
        user: UserEntity,
        product: ProductEntity?,
        recommendation: GiftRecommendationEntity?,
        customTitle: String?,
        customUrl: String?,
        notes: String?
    ): WishlistItemEntity =
        WishlistItemEntity(
            user = user,
            product = product,
            recommendation = recommendation,
            customTitle = customTitle,
            customUrl = customUrl,
            notes = notes
        )
}