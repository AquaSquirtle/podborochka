package org.podborochka.wishlist.infrastructure.repository

import org.podborochka.wishlist.infrastructure.entity.WishlistItemEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface WishlistItemRepository : JpaRepository<WishlistItemEntity, Long> {

    fun findAllByUserIdOrderByCreatedAtDesc(
        userId: Long,
        pageable: Pageable
    ): Page<WishlistItemEntity>

    fun findByIdAndUserId(id: Long, userId: Long): WishlistItemEntity?
}