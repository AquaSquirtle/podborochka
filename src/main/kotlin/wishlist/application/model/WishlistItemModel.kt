package org.podborochka.wishlist.application.model

import java.time.LocalDateTime

data class WishlistItemModel(
    val id: Long,
    val userId: Long,
    val productId: Long?,
    val recommendationId: Long?,
    val customTitle: String?,
    val customUrl: String?,
    val notes: String?,
    val isPurchased: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)