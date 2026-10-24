package org.podborochka.conversation.controller

import org.podborochka.common.security.AppUserPrincipal
import org.podborochka.common.web.utils.PaginationUtils
import org.podborochka.conversation.application.service.ConversationService
import org.podborochka.conversation.controller.dto.ConversationResponse
import org.podborochka.conversation.controller.dto.CreateConversationRequest
import org.podborochka.conversation.controller.dto.UpdateConversationRequest
import org.podborochka.conversation.controller.mapper.ConversationControllerMapper
import org.podborochka.graphql.dto.ConversationMessagesCarrier
import org.podborochka.graphql.dto.ConversationPageGql
import org.podborochka.graphql.dto.MessagePageGql
import org.podborochka.graphql.dto.CreateConversationInputGql
import org.podborochka.graphql.dto.UpdateConversationInputGql
import org.podborochka.message.application.service.MessageService
import org.podborochka.message.controller.mapper.MessageControllerMapper
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller

@Controller
class ConversationGraphQlController(
    private val conversationService: ConversationService,
    private val conversationControllerMapper: ConversationControllerMapper,
    private val messageService: MessageService,
    private val messageControllerMapper: MessageControllerMapper
) {

    @QueryMapping
    fun conversations(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Argument page: Int?,
        @Argument size: Int?
    ): ConversationPageGql {
        val pageable = PaginationUtils.pageable(page, size)
        val result = conversationService.getAllByUser(principal.id, pageable)

        return ConversationPageGql(
            content = result.content.map(conversationControllerMapper::toResponse),
            page = result.number,
            size = result.size,
            totalElements = result.totalElements.toInt(),
            totalPages = result.totalPages
        )
    }

    @QueryMapping
    fun conversation(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Argument id: Long
    ): ConversationMessagesCarrier {
        val conversation = conversationControllerMapper.toResponse(
            conversationService.getById(id, principal.id)
        )

        return conversation.toCarrier()
    }

    @MutationMapping
    fun createConversation(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Argument input: CreateConversationInputGql
    ): ConversationResponse =
        conversationControllerMapper.toResponse(
            conversationService.create(
                conversationControllerMapper.toCreateCommand(
                    principal.id,
                    CreateConversationRequest(title = input.title)
                )
            )
        )

    @MutationMapping
    fun updateConversation(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Argument id: Long,
        @Argument input: UpdateConversationInputGql
    ): ConversationResponse =
        conversationControllerMapper.toResponse(
            conversationService.update(
                id,
                principal.id,
                conversationControllerMapper.toUpdateCommand(
                    UpdateConversationRequest(title = input.title)
                )
            )
        )

    @MutationMapping
    fun deleteConversation(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Argument id: Long
    ): Boolean {
        conversationService.delete(id, principal.id)
        return true
    }

    @SchemaMapping(typeName = "Conversation", field = "messages")
    fun messages(
        conversation: ConversationMessagesCarrier,
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Argument page: Int?,
        @Argument size: Int?
    ): MessagePageGql {
        val pageable = PaginationUtils.pageable(page, size)
        val result = messageService.getAllByConversation(conversation.id, principal.id, pageable)

        return MessagePageGql(
            content = result.content.map(messageControllerMapper::toResponse),
            page = result.number,
            size = result.size,
            totalElements = result.totalElements.toInt(),
            totalPages = result.totalPages
        )
    }

    private fun ConversationResponse.toCarrier() =
        ConversationMessagesCarrier(
            id = id,
            title = title,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}