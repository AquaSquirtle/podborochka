package org.podborochka.chat.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.podborochka.chat.application.service.ChatService
import org.podborochka.chat.controller.dto.SendChatMessageRequest
import org.podborochka.chat.controller.dto.SendChatMessageResponse
import org.podborochka.chat.controller.mapper.ChatControllerMapper
import org.podborochka.common.security.AppUserPrincipal
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/chat")
@Tag(
    name = "Chat",
    description = "Основной API общения пользователя с ИИ для подбора подарков"
)
@SecurityRequirement(name = "sessionAuth")
class ChatController(
    private val chatService: ChatService,
    private val mapper: ChatControllerMapper
) {

    @Operation(
        summary = "Отправить сообщение в чат",
        description = """
            Основной endpoint приложения.
            
            Выполняет:
            - сохранение пользовательского сообщения
            - извлечение intent через LLM
            - создание GiftSearchRequest
            - создание GiftRecommendation
            - сохранение ответа ассистента
            
            Возвращает текст ответа ассистента и список рекомендаций.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Ответ успешно сгенерирован"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации запроса"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            ApiResponse(responseCode = "404", description = "Диалог не найден")
        ]
    )
    @PostMapping("/{conversationId}/message")
    fun sendMessage(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Parameter(description = "Идентификатор диалога")
        @PathVariable conversationId: Long,
        @Valid @RequestBody request: SendChatMessageRequest
    ): SendChatMessageResponse {
        val command = mapper.toCommand(principal.id, conversationId, request)
        val result = chatService.processMessage(command)
        return mapper.toResponse(result)
    }
}