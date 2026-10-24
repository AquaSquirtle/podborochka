package org.podborochka.giftsearchrequest.controller

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
import org.podborochka.giftsearchrequest.application.service.GiftSearchRequestService
import org.podborochka.giftsearchrequest.controller.dto.CreateGiftSearchRequestRequest
import org.podborochka.giftsearchrequest.controller.dto.GiftSearchRequestResponse
import org.podborochka.giftsearchrequest.controller.dto.UpdateGiftSearchRequestRequest
import org.podborochka.giftsearchrequest.controller.mapper.GiftSearchRequestControllerMapper
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@Tag(
    name = "Gift Search Requests",
    description = "Структурированные запросы на подбор подарков"
)
@SecurityRequirement(name = "sessionAuth")
class GiftSearchRequestController(
    private val service: GiftSearchRequestService,
    private val mapper: GiftSearchRequestControllerMapper,
    private val pageResponseMapper: PageResponseMapper
) {

    @Operation(
        summary = "Создать запрос на подбор подарка",
        description = """
            Создает gift search request внутри указанного диалога.
            
            Эта сущность содержит нормализованную структуру запроса:
            - rawPrompt
            - описание получателя
            - occasion
            - бюджет
            - интересы
            - статус обработки
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Запрос успешно создан"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации запроса"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Диалог не найден")
        ]
    )
    @PostMapping("/api/v1/conversations/{conversationId}/gift-search-requests")
    fun create(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор диалога")
        @PathVariable conversationId: Long,
        @Valid @RequestBody request: CreateGiftSearchRequestRequest
    ): GiftSearchRequestResponse {
        val command = mapper.toCreateCommand(principal.id, conversationId, request)
        val model = service.create(command)
        return mapper.toResponse(model)
    }

    @Operation(
        summary = "Получить запросы по диалогу",
        description = """
            Возвращает все gift search requests, связанные с указанным conversation.
            
            Поддерживает пагинацию и используется для просмотра истории структурированных запросов
            внутри конкретного диалога.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Список запросов получен"),
            ApiResponse(responseCode = "400", description = "Некорректные параметры пагинации"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Диалог не найден")
        ]
    )
    @GetMapping("/api/v1/conversations/{conversationId}/gift-search-requests")
    fun getAllByConversation(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор диалога")
        @PathVariable conversationId: Long,
        @Parameter(description = "Номер страницы, начиная с 0")
        @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Размер страницы")
        @RequestParam(defaultValue = "20") size: Int
    ): PageResponse<GiftSearchRequestResponse> {
        val pageable = PaginationUtils.pageable(page, size)
        val result = service.getAllByConversation(conversationId, principal.id, pageable)
        return pageResponseMapper.toPageResponse(result, mapper::toResponse)
    }

    @Operation(
        summary = "Получить все запросы пользователя",
        description = """
            Возвращает все gift search requests текущего пользователя с пагинацией.
            
            Используется для аналитики, истории запросов и управления ранее сформированными подборками.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Список запросов получен"),
            ApiResponse(responseCode = "400", description = "Некорректные параметры пагинации"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
        ]
    )
    @GetMapping("/api/v1/gift-search-requests")
    fun getAllByUser(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Номер страницы, начиная с 0")
        @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Размер страницы")
        @RequestParam(defaultValue = "20") size: Int
    ): PageResponse<GiftSearchRequestResponse> {
        val pageable = PaginationUtils.pageable(page, size)
        val result = service.getAllByUser(principal.id, pageable)
        return pageResponseMapper.toPageResponse(result, mapper::toResponse)
    }

    @Operation(
        summary = "Получить запрос по id",
        description = "Возвращает один gift search request текущего пользователя"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Запрос найден"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Запрос не найден")
        ]
    )
    @GetMapping("/api/v1/gift-search-requests/{id}")
    fun getById(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор запроса")
        @PathVariable id: Long
    ): GiftSearchRequestResponse {
        val model = service.getById(id, principal.id)
        return mapper.toResponse(model)
    }

    @Operation(
        summary = "Обновить запрос",
        description = """
            Обновляет существующий gift search request.
            
            Может использоваться для изменения:
            - recipientDescription
            - occasion
            - budgetMin / budgetMax
            - interests
            - status
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Запрос обновлен"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации запроса"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Запрос не найден")
        ]
    )
    @PutMapping("/api/v1/gift-search-requests/{id}")
    fun update(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор запроса")
        @PathVariable id: Long,
        @RequestBody request: UpdateGiftSearchRequestRequest
    ): GiftSearchRequestResponse {
        val command = mapper.toUpdateCommand(request)
        val model = service.update(id, principal.id, command)
        return mapper.toResponse(model)
    }

    @Operation(
        summary = "Удалить запрос",
        description = "Удаляет gift search request текущего пользователя"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Запрос удален"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Запрос не найден")
        ]
    )
    @DeleteMapping("/api/v1/gift-search-requests/{id}")
    fun delete(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор запроса")
        @PathVariable id: Long
    ) {
        service.delete(id, principal.id)
    }
}