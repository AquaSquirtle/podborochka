package org.podborochka.wishlist.controller
import org.podborochka.common.security.AppUserPrincipal
import org.podborochka.common.web.utils.PaginationUtils
import org.podborochka.graphql.dto.SaveWishlistItemInputGql
import org.podborochka.graphql.dto.WishlistItemPageGql
import org.podborochka.wishlist.application.service.WishlistItemService
import org.podborochka.wishlist.controller.dto.CreateWishlistItemRequest
import org.podborochka.wishlist.controller.mapper.WishlistControllerMapper
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller

@Controller
class WishlistGraphQlController(
    private val wishlistItemService: WishlistItemService,
    private val wishlistControllerMapper: WishlistControllerMapper
) {

    @QueryMapping
    fun wishlistItems(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Argument page: Int?,
        @Argument size: Int?
    ): WishlistItemPageGql {
        val pageable = PaginationUtils.pageable(page, size)
        val result = wishlistItemService.getAll(principal.id, pageable)

        return WishlistItemPageGql(
            content = result.content.map(wishlistControllerMapper::toResponse),
            page = result.number,
            size = result.size,
            totalElements = result.totalElements.toInt(),
            totalPages = result.totalPages
        )
    }

    @MutationMapping
    fun saveWishlistItem(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Argument input: SaveWishlistItemInputGql
    ) = wishlistControllerMapper.toResponse(
        wishlistItemService.create(
            wishlistControllerMapper.toCreateCommand(
                principal.id,
                CreateWishlistItemRequest(
                    productId = input.productId,
                    recommendationId = input.recommendationId,
                    customTitle = input.customTitle,
                    customUrl = input.customUrl,
                    notes = input.notes
                )
            )
        )
    )

    @MutationMapping
    fun deleteWishlistItem(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Argument id: Long
    ): Boolean {
        wishlistItemService.delete(id, principal.id)
        return true
    }
}