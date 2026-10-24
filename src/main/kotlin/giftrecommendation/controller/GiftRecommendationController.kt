package org.podborochka.giftrecommendation.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.podborochka.common.security.AppUserPrincipal
import org.podborochka.common.web.dto.PageResponse
import org.podborochka.common.web.mapper.PageResponseMapper
import org.podborochka.common.web.utils.PaginationUtils
import org.podborochka.giftrecommendation.application.service.GiftRecommendationService
import org.podborochka.giftrecommendation.controller.dto.CreateGiftRecommendationRequest
import org.podborochka.giftrecommendation.controller.dto.GiftRecommendationResponse
import org.podborochka.giftrecommendation.controller.dto.UpdateGiftRecommendationRequest
import org.podborochka.giftrecommendation.controller.mapper.GiftRecommendationControllerMapper
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@Tag(
    name = "Gift Recommendations",
    description = "Рекомендации подарков, связанные с запросами пользователя"
)
@SecurityRequirement(name = "sessionAuth")
class GiftRecommendationController(
    private val service: GiftRecommendationService,
    private val mapper: GiftRecommendationControllerMapper,
    private val pageResponseMapper: PageResponseMapper
) {

    @Operation(
        summary = "Создать рекомендацию",
        description = """
            Создает рекомендацию для конкретного gift search request.
            
            Обычно рекомендация создается после обработки пользовательского запроса моделью LLM,
            но этот endpoint также может использоваться вручную или внутренними сервисами.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Рекомендация создана"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации запроса"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Gift search request не найден")
        ]
    )
    @PostMapping("/api/v1/gift-search-requests/{searchRequestId}/recommendations")
    fun create(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор gift search request")
        @PathVariable searchRequestId: Long,
        @Valid @RequestBody request: CreateGiftRecommendationRequest
    ): GiftRecommendationResponse {
        val command = mapper.toCreateCommand(principal.id, searchRequestId, request)
        val model = service.create(command)
        return mapper.toResponse(model)
    }

    @Operation(
        summary = "Получить рекомендации по запросу",
        description = """
            Возвращает все рекомендации, связанные с конкретным gift search request.
            
            Поддерживает пагинацию и используется для отображения результатов подбора подарков.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Список рекомендаций получен"),
            ApiResponse(responseCode = "400", description = "Некорректные параметры пагинации"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Gift search request не найден")
        ]
    )
    @GetMapping("/api/v1/gift-search-requests/{searchRequestId}/recommendations")
    fun getAllBySearchRequest(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор gift search request")
        @PathVariable searchRequestId: Long,
        @Parameter(description = "Номер страницы, начиная с 0")
        @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Размер страницы")
        @RequestParam(defaultValue = "20") size: Int
    ): PageResponse<GiftRecommendationResponse> {
        val pageable = PaginationUtils.pageable(page, size)
        val result = service.getAllBySearchRequest(searchRequestId, principal.id, pageable)
        return pageResponseMapper.toPageResponse(result, mapper::toResponse)
    }

    @Operation(
        summary = "Получить рекомендацию по id",
        description = "Возвращает одну gift recommendation текущего пользователя"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Рекомендация найдена"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Рекомендация не найдена")
        ]
    )
    @GetMapping("/api/v1/gift-recommendations/{id}")
    fun getById(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор рекомендации")
        @PathVariable id: Long
    ): GiftRecommendationResponse {
        val model = service.getById(id, principal.id)
        return mapper.toResponse(model)
    }

    @Operation(
        summary = "Обновить рекомендацию",
        description = """
            Обновляет существующую рекомендацию.
            
            Может использоваться для изменения:
            - title
            - description
            - reasoning
            - rankPosition
            - productId
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Рекомендация обновлена"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации запроса"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Рекомендация не найдена")
        ]
    )
    @PutMapping("/api/v1/gift-recommendations/{id}")
    fun update(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор рекомендации")
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateGiftRecommendationRequest
    ): GiftRecommendationResponse {
        val command = mapper.toUpdateCommand(request)
        val model = service.update(id, principal.id, command)
        return mapper.toResponse(model)
    }

    @Operation(
        summary = "Удалить рекомендацию",
        description = "Удаляет gift recommendation текущего пользователя"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Рекомендация удалена"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Рекомендация не найдена")
        ]
    )
    @DeleteMapping("/api/v1/gift-recommendations/{id}")
    fun delete(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор рекомендации")
        @PathVariable id: Long
    ) {
        service.delete(id, principal.id)
    }
}