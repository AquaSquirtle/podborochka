package org.podborochka.wishlist.controller.dto

data class WishlistItemResponse(
    val id: Long,
    val productId: Long?,
    val recommendationId: Long?,
    val customTitle: String?,
    val customUrl: String?,
    val notes: String?,
    val isPurchased: Boolean,
    val createdAt: String
)