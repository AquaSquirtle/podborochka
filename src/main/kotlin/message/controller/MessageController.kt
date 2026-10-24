package org.podborochka.message.controller

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
import org.podborochka.message.application.service.MessageService
import org.podborochka.message.controller.dto.CreateMessageRequest
import org.podborochka.message.controller.dto.MessageResponse
import org.podborochka.message.controller.mapper.MessageControllerMapper
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/v1/conversations/{conversationId}/messages")
@Tag(
    name = "Messages",
    description = "Сообщения внутри пользовательских диалогов"
)
@SecurityRequirement(name = "sessionAuth")
class MessageController(
    private val messageService: MessageService,
    private val mapper: MessageControllerMapper,
    private val pageResponseMapper: PageResponseMapper
) {

    @Operation(
        summary = "Создать сообщение",
        description = """
            Добавляет новое сообщение в указанный conversation.
            
            Обычно сообщения создаются:
            - пользователем
            - ассистентом
            - системой
            
            В рабочем сценарии основная отправка сообщений происходит через chat endpoint,
            однако данный метод позволяет работать с сообщениями напрямую.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Сообщение создано"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации запроса"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Диалог не найден")
        ]
    )
    @PostMapping
    fun create(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор диалога")
        @PathVariable conversationId: Long,
        @Valid @RequestBody request: CreateMessageRequest
    ): MessageResponse {
        val command = mapper.toCreateCommand(principal.id, conversationId, request)
        val model = messageService.create(command)
        return mapper.toResponse(model)
    }

    @Operation(
        summary = "Получить сообщения диалога",
        description = """
            Возвращает историю сообщений в указанном диалоге с пагинацией.
            
            Сообщения отсортированы по времени создания и используются для:
            - восстановления истории чата
            - отображения интерфейса переписки
            - отладки или повторного анализа диалога
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Сообщения получены"),
            ApiResponse(responseCode = "400", description = "Некорректные параметры пагинации"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Диалог не найден")
        ]
    )
    @GetMapping
    fun getAll(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор диалога")
        @PathVariable conversationId: Long,
        @Parameter(description = "Номер страницы, начиная с 0")
        @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Размер страницы")
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<MessageResponse>> {
        val pageable = PaginationUtils.pageable(page, size)
        val result = messageService.getAllByConversation(conversationId, principal.id, pageable)
        val body = pageResponseMapper.toPageResponse(result, mapper::toResponse)

        return ResponseEntity.ok()
            .cacheControl(
                CacheControl.maxAge(10, TimeUnit.SECONDS)
                    .cachePrivate()
                    .mustRevalidate()
            )
            .body(body)
    }

    @Operation(
        summary = "Получить сообщение по id",
        description = "Возвращает одно сообщение из указанного диалога"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Сообщение найдено"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Сообщение не найдено")
        ]
    )
    @GetMapping("/{id}")
    fun getById(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор диалога")
        @PathVariable conversationId: Long,
        @Parameter(description = "Идентификатор сообщения")
        @PathVariable id: Long
    ): MessageResponse {
        val model = messageService.getById(id, conversationId, principal.id)
        return mapper.toResponse(model)
    }

    @Operation(
        summary = "Удалить сообщение",
        description = "Удаляет сообщение из указанного диалога"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Сообщение удалено"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Сообщение не найдено")
        ]
    )
    @DeleteMapping("/{id}")
    fun delete(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор диалога")
        @PathVariable conversationId: Long,
        @Parameter(description = "Идентификатор сообщения")
        @PathVariable id: Long
    ) {
        messageService.delete(id, conversationId, principal.id)
    }
}