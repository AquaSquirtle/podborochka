package org.podborochka.wishlist.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.podborochka.common.security.AppUserPrincipal
import org.podborochka.wishlist.application.service.WishlistNotificationService
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter


@RestController
@RequestMapping("/api/v1/wishlist-notifications")
@SecurityRequirement(name = "sessionAuth")
class WishlistNotificationController(
    private val wishlistNotificationService: WishlistNotificationService
) {

    @GetMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun subscribe(
        @AuthenticationPrincipal principal: AppUserPrincipal
    ): SseEmitter {
        return wishlistNotificationService.subscribe(principal.id)
    }
}