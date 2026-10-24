package org.podborochka.wishlist.controller.mapper

import org.podborochka.wishlist.application.model.CreateWishlistItemCommand
import org.podborochka.wishlist.application.model.UpdateWishlistItemCommand
import org.podborochka.wishlist.application.model.WishlistItemModel
import org.podborochka.wishlist.controller.dto.CreateWishlistItemRequest
import org.podborochka.wishlist.controller.dto.UpdateWishlistItemRequest
import org.podborochka.wishlist.controller.dto.WishlistItemResponse
import org.springframework.stereotype.Component

@Component
class WishlistControllerMapper {

    fun toCreateCommand(
        userId: Long,
        request: CreateWishlistItemRequest
    ): CreateWishlistItemCommand =
        CreateWishlistItemCommand(
            userId = userId,
            productId = request.productId,
            recommendationId = request.recommendationId,
            customTitle = request.customTitle,
            customUrl = request.customUrl,
            notes = request.notes
        )

    fun toUpdateCommand(
        request: UpdateWishlistItemRequest
    ): UpdateWishlistItemCommand =
        UpdateWishlistItemCommand(
            customTitle = request.customTitle,
            customUrl = request.customUrl,
            notes = request.notes,
            isPurchased = request.isPurchased
        )

    fun toResponse(
        model: WishlistItemModel
    ): WishlistItemResponse =
        WishlistItemResponse(
            id = model.id,
            productId = model.productId,
            recommendationId = model.recommendationId,
            customTitle = model.customTitle,
            customUrl = model.customUrl,
            notes = model.notes,
            isPurchased = model.isPurchased,
            createdAt = model.createdAt.toString()
        )
}