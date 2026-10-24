package org.podborochka.conversation.controller

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
import org.podborochka.conversation.application.service.ConversationService
import org.podborochka.conversation.controller.dto.ConversationResponse
import org.podborochka.conversation.controller.dto.CreateConversationRequest
import org.podborochka.conversation.controller.dto.UpdateConversationRequest
import org.podborochka.conversation.controller.mapper.ConversationControllerMapper
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/conversations")
@Tag(
    name = "Conversations",
    description = "Управление диалогами пользователя с ИИ"
)
@SecurityRequirement(name = "sessionAuth")
class ConversationController(
    private val service: ConversationService,
    private val mapper: ConversationControllerMapper,
    private val pageResponseMapper: PageResponseMapper
) {

    @Operation(
        summary = "Создать новый диалог",
        description = """
            Создает новый conversation для текущего пользователя.
            
            Диалог является контейнером для:
            - сообщений пользователя и ассистента
            - запросов на подбор подарков
            - рекомендаций, связанных с данным диалогом
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Диалог успешно создан"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
        ]
    )
    @PostMapping
    fun create(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @RequestBody request: CreateConversationRequest
    ): ConversationResponse {
        val command = mapper.toCreateCommand(principal.id, request)
        val model = service.create(command)
        return mapper.toResponse(model)
    }

    @Operation(
        summary = "Получить список диалогов пользователя",
        description = """
            Возвращает список conversation текущего пользователя с пагинацией.
            
            Поддерживает параметры:
            - page — номер страницы, начиная с 0
            - size — размер страницы
            
            Используется для отображения истории чатов и списка сохраненных диалогов.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Список диалогов получен"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "400", description = "Некорректные параметры пагинации")
        ]
    )
    @GetMapping
    fun getAll(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Номер страницы, начиная с 0")
        @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Размер страницы")
        @RequestParam(defaultValue = "20") size: Int
    ): PageResponse<ConversationResponse> {
        val pageable = PaginationUtils.pageable(page, size)
        val result = service.getAllByUser(principal.id, pageable)
        return pageResponseMapper.toPageResponse(result, mapper::toResponse)
    }

    @Operation(
        summary = "Получить диалог по id",
        description = """
            Возвращает конкретный диалог текущего пользователя.
            
            Используется при открытии выбранного чата и восстановлении его состояния.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Диалог найден"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Диалог не найден")
        ]
    )
    @GetMapping("/{id}")
    fun getById(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор диалога")
        @PathVariable id: Long
    ): ConversationResponse {
        val model = service.getById(id, principal.id)
        return mapper.toResponse(model)
    }

    @Operation(
        summary = "Обновить диалог",
        description = """
            Позволяет изменить параметры диалога, например title.
            
            Используется для переименования чата или изменения его метаданных.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Диалог обновлен"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Диалог не найден")
        ]
    )
    @PutMapping("/{id}")
    fun update(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор диалога")
        @PathVariable id: Long,
        @RequestBody request: UpdateConversationRequest
    ): ConversationResponse {
        val command = mapper.toUpdateCommand(request)
        val model = service.update(id, principal.id, command)
        return mapper.toResponse(model)
    }

    @Operation(
        summary = "Удалить диалог",
        description = """
            Удаляет conversation текущего пользователя.
            
            В зависимости от каскадных настроек также удаляются или становятся недоступными
            связанные данные: сообщения, search requests и рекомендации.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Диалог удален"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Диалог не найден")
        ]
    )
    @DeleteMapping("/{id}")
    fun delete(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор диалога")
        @PathVariable id: Long
    ) {
        service.delete(id, principal.id)
    }
}