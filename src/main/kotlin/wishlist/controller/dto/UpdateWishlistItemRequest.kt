package org.podborochka.wishlist.controller.dto

data class UpdateWishlistItemRequest(
    val customTitle: String? = null,
    val customUrl: String? = null,
    val notes: String? = null,
    val isPurchased: Boolean? = null
)