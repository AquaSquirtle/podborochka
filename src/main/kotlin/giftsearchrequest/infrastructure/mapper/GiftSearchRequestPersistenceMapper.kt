package org.podborochka.giftsearchrequest.infrastructure.mapper

import org.podborochka.conversation.infrastructure.entity.ConversationEntity
import org.podborochka.giftsearchrequest.application.model.GiftSearchRequestModel
import org.podborochka.giftsearchrequest.domain.GiftSearchRequestStatus
import org.podborochka.giftsearchrequest.infrastructure.entity.GiftSearchRequestEntity
import org.podborochka.user.infrastructure.entity.UserEntity
import org.springframework.stereotype.Component

@Component
class GiftSearchRequestPersistenceMapper {

    fun toModel(entity: GiftSearchRequestEntity): GiftSearchRequestModel =
        GiftSearchRequestModel(
            id = requireNotNull(entity.id),
            conversationId = requireNotNull(entity.conversation.id),
            userId = requireNotNull(entity.user.id),
            rawPrompt = entity.rawPrompt,
            recipientDescription = entity.recipientDescription,
            occasion = entity.occasion,
            budgetMin = entity.budgetMin,
            budgetMax = entity.budgetMax,
            interests = entity.interests,
            status = entity.status,
            createdAt = requireNotNull(entity.createdAt),
            updatedAt = requireNotNull(entity.updatedAt)
        )

    fun toNewEntity(
        conversation: ConversationEntity,
        user: UserEntity,
        rawPrompt: String,
        recipientDescription: String?,
        occasion: String?,
        budgetMin: java.math.BigDecimal?,
        budgetMax: java.math.BigDecimal?,
        interests: String?
    ): GiftSearchRequestEntity =
        GiftSearchRequestEntity(
            conversation = conversation,
            user = user,
            rawPrompt = rawPrompt,
            recipientDescription = recipientDescription,
            occasion = occasion,
            budgetMin = budgetMin,
            budgetMax = budgetMax,
            interests = interests,
            status = GiftSearchRequestStatus.CREATED
        )
}