package org.podborochka.wishlist.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.podborochka.common.security.AppUserPrincipal
import org.podborochka.common.web.dto.PageResponse
import org.podborochka.common.web.mapper.PageResponseMapper
import org.podborochka.common.web.utils.PaginationUtils
import org.podborochka.wishlist.application.service.WishlistItemService
import org.podborochka.wishlist.controller.dto.CreateWishlistItemRequest
import org.podborochka.wishlist.controller.dto.UpdateWishlistItemRequest
import org.podborochka.wishlist.controller.dto.WishlistItemResponse
import org.podborochka.wishlist.controller.mapper.WishlistControllerMapper
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/wishlist-items")
@Tag(
    name = "Wishlist",
    description = "Управление сохраненными подарками пользователя"
)
@SecurityRequirement(name = "sessionAuth")
class WishlistItemController(
    private val service: WishlistItemService,
    private val mapper: WishlistControllerMapper,
    private val pageResponseMapper: PageResponseMapper
) {

    @Operation(
        summary = "Добавить элемент в wishlist",
        description = """
            Добавляет подарок в список сохраненных пользователя.
            
            Элемент можно создать:
            - на основе product
            - на основе recommendation
            - как кастомный подарок вручную
            
            Также могут сохраняться notes, customTitle и customUrl.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Элемент wishlist создан"),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Связанный объект не найден")
        ]
    )
    @PostMapping
    fun create(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @RequestBody request: CreateWishlistItemRequest
    ): WishlistItemResponse {
        val command = mapper.toCreateCommand(principal.id, request)
        return mapper.toResponse(service.create(command))
    }

    @Operation(
        summary = "Получить wishlist пользователя",
        description = """
            Возвращает список сохраненных подарков текущего пользователя с пагинацией.
            
            Используется для отображения избранного, списка желаемых подарков и последующего управления ими.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Wishlist получен"),
            ApiResponse(responseCode = "400", description = "Некорректные параметры пагинации"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
        ]
    )
    @GetMapping
    fun getAll(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Номер страницы, начиная с 0")
        @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Размер страницы")
        @RequestParam(defaultValue = "20") size: Int
    ): PageResponse<WishlistItemResponse> {
        val pageable = PaginationUtils.pageable(page, size)
        val result = service.getAll(principal.id, pageable)
        return pageResponseMapper.toPageResponse(result, mapper::toResponse)
    }

    @Operation(
        summary = "Получить элемент wishlist по id",
        description = "Возвращает один элемент wishlist текущего пользователя"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Элемент найден"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Элемент не найден")
        ]
    )
    @GetMapping("/{id}")
    fun getById(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор элемента wishlist")
        @PathVariable id: Long
    ): WishlistItemResponse =
        mapper.toResponse(service.getById(id, principal.id))

    @Operation(
        summary = "Обновить элемент wishlist",
        description = """
            Позволяет обновить данные элемента wishlist.
            
            Можно изменить:
            - customTitle
            - customUrl
            - notes
            - isPurchased
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Элемент обновлен"),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Элемент не найден")
        ]
    )
    @PutMapping("/{id}")
    fun update(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор элемента wishlist")
        @PathVariable id: Long,
        @RequestBody request: UpdateWishlistItemRequest
    ): WishlistItemResponse =
        mapper.toResponse(service.update(id, principal.id, mapper.toUpdateCommand(request)))

    @Operation(
        summary = "Удалить элемент wishlist",
        description = "Удаляет элемент из списка сохраненных подарков"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Элемент удален"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Элемент не найден")
        ]
    )
    @DeleteMapping("/{id}")
    fun delete(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор элемента wishlist")
        @PathVariable id: Long
    ) {
        service.delete(id, principal.id)
    }
}