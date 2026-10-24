package org.podborochka.conversation.controller.mapper


import org.podborochka.conversation.application.model.ConversationModel
import org.podborochka.conversation.application.model.CreateConversationCommand
import org.podborochka.conversation.application.model.UpdateConversationCommand
import org.podborochka.conversation.controller.dto.ConversationResponse
import org.podborochka.conversation.controller.dto.CreateConversationRequest
import org.podborochka.conversation.controller.dto.UpdateConversationRequest
import org.springframework.stereotype.Component

@Component
class ConversationControllerMapper {

    fun toCreateCommand(userId: Long, request: CreateConversationRequest) =
        CreateConversationCommand(
            userId = userId,
            title = request.title
        )

    fun toUpdateCommand(request: UpdateConversationRequest) =
        UpdateConversationCommand(
            title = request.title
        )

    fun toResponse(model: ConversationModel) =
        ConversationResponse(
            id = model.id,
            title = model.title,
            createdAt = model.createdAt.toString(),
            updatedAt = model.updatedAt.toString()
        )
}