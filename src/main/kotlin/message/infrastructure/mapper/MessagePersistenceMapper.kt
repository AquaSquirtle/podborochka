package org.podborochka.message.infrastructure.mapper

import org.podborochka.conversation.infrastructure.entity.ConversationEntity
import org.podborochka.message.application.model.MessageModel
import org.podborochka.message.domain.MessageRole
import org.podborochka.message.infrastructure.entity.MessageEntity
import org.springframework.stereotype.Component


@Component
class MessagePersistenceMapper {

    fun toModel(entity: MessageEntity): MessageModel =
        MessageModel(
            id = requireNotNull(entity.id),
            conversationId = requireNotNull(entity.conversation.id),
            role = MessageRole.valueOf(entity.role.uppercase()),
            content = entity.content,
            createdAt = requireNotNull(entity.createdAt)
        )

    fun toNewEntity(
        conversation: ConversationEntity,
        role: MessageRole,
        content: String
    ): MessageEntity =
        MessageEntity(
            conversation = conversation,
            role = role.name.lowercase(),
            content = content
        )
}