package org.podborochka.conversation.infrastructure.mapper

import org.podborochka.conversation.application.model.ConversationModel
import org.podborochka.conversation.infrastructure.entity.ConversationEntity
import org.podborochka.user.infrastructure.entity.UserEntity
import org.springframework.stereotype.Component

@Component
class ConversationPersistenceMapper {

    fun toModel(entity: ConversationEntity): ConversationModel =
        ConversationModel(
            id = requireNotNull(entity.id),
            userId = requireNotNull(entity.user.id),
            title = entity.title,
            createdAt = requireNotNull(entity.createdAt),
            updatedAt = requireNotNull(entity.updatedAt)
        )

    fun toNewEntity(
        user: UserEntity,
        title: String?
    ): ConversationEntity =
        ConversationEntity(
            user = user,
            title = title
        )
}