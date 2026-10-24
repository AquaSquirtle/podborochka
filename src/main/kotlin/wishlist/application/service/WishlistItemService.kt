package org.podborochka.wishlist.application.service

import org.podborochka.wishlist.application.model.CreateWishlistItemCommand
import org.podborochka.wishlist.application.model.UpdateWishlistItemCommand
import org.podborochka.wishlist.application.model.WishlistItemModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface WishlistItemService {

    fun create(command: CreateWishlistItemCommand): WishlistItemModel

    fun getAll(userId: Long, pageable: Pageable): Page<WishlistItemModel>

    fun getById(id: Long, userId: Long): WishlistItemModel

    fun update(id: Long, userId: Long, command: UpdateWishlistItemCommand): WishlistItemModel

    fun delete(id: Long, userId: Long)
}