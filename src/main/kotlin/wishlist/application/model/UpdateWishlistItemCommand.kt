package org.podborochka.wishlist.application.model

data class UpdateWishlistItemCommand(
    val productId: Long? = null,
    val recommendationId: Long? = null,
    val customTitle: String? = null,
    val customUrl: String? = null,
    val notes: String? = null,
    val isPurchased: Boolean? = null
)