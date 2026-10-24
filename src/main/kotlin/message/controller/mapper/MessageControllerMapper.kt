package org.podborochka.message.controller.mapper

import org.podborochka.message.application.model.CreateMessageCommand
import org.podborochka.message.application.model.MessageModel
import org.podborochka.message.controller.dto.CreateMessageRequest
import org.podborochka.message.controller.dto.MessageResponse
import org.springframework.stereotype.Component

@Component
class MessageControllerMapper {

    fun toCreateCommand(
        userId: Long,
        conversationId: Long,
        request: CreateMessageRequest
    ): CreateMessageCommand =
        CreateMessageCommand(
            userId = userId,
            conversationId = conversationId,
            role = request.role,
            content = request.content
        )

    fun toResponse(model: MessageModel): MessageResponse =
        MessageResponse(
            id = model.id,
            conversationId = model.conversationId,
            role = model.role.name,
            content = model.content,
            createdAt = model.createdAt.toString()
        )
}