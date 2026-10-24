package org.podborochka.wishlist.application.service

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

interface WishlistNotificationService {
    fun subscribe(userId: Long): SseEmitter
    fun notifyWishlistSaved(userId: Long, title: String)
}