package org.podborochka.wishlist.application.model

data class CreateWishlistItemCommand(
    val userId: Long,
    val productId: Long? = null,
    val recommendationId: Long? = null,
    val customTitle: String? = null,
    val customUrl: String? = null,
    val notes: String? = null
)