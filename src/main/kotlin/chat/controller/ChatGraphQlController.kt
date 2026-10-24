package org.podborochka.chat.controller
import org.podborochka.chat.application.service.ChatService
import org.podborochka.chat.controller.dto.SendChatMessageRequest
import org.podborochka.chat.controller.mapper.ChatControllerMapper
import org.podborochka.common.security.AppUserPrincipal
import org.podborochka.graphql.dto.SendChatMessagePayloadGql
import org.podborochka.graphql.dto.SendMessageInputGql
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller

@Controller
class ChatGraphQlController(
    private val chatService: ChatService,
    private val chatControllerMapper: ChatControllerMapper
) {

    @MutationMapping
    fun sendMessage(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Argument conversationId: Long,
        @Argument input: SendMessageInputGql
    ): SendChatMessagePayloadGql {
        val response = chatControllerMapper.toResponse(
            chatService.processMessage(
                chatControllerMapper.toCommand(
                    principal.id,
                    conversationId,
                    SendChatMessageRequest(prompt = input.prompt)
                )
            )
        )

        return SendChatMessagePayloadGql(
            conversationId = response.conversationId,
            prompt = response.prompt,
            assistantMessage = response.assistantMessage,
            recipientSummary = response.recipientSummary,
            recommendations = response.recommendations
        )
    }
}