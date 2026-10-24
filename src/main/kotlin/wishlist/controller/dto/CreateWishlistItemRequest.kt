package org.podborochka.wishlist.controller.dto

data class CreateWishlistItemRequest(
    val productId: Long? = null,
    val recommendationId: Long? = null,
    val customTitle: String? = null,
    val customUrl: String? = null,
    val notes: String? = null
)