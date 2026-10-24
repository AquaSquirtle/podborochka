package org.podborochka.giftsearchrequest.controller.mapper

import org.podborochka.giftsearchrequest.application.model.CreateGiftSearchRequestCommand
import org.podborochka.giftsearchrequest.application.model.GiftSearchRequestModel
import org.podborochka.giftsearchrequest.application.model.UpdateGiftSearchRequestCommand
import org.podborochka.giftsearchrequest.controller.dto.CreateGiftSearchRequestRequest
import org.podborochka.giftsearchrequest.controller.dto.GiftSearchRequestResponse
import org.podborochka.giftsearchrequest.controller.dto.UpdateGiftSearchRequestRequest
import org.springframework.stereotype.Component

@Component
class GiftSearchRequestControllerMapper {

    fun toCreateCommand(
        userId: Long,
        conversationId: Long,
        request: CreateGiftSearchRequestRequest
    ): CreateGiftSearchRequestCommand =
        CreateGiftSearchRequestCommand(
            userId = userId,
            conversationId = conversationId,
            rawPrompt = request.rawPrompt,
            recipientDescription = request.recipientDescription,
            occasion = request.occasion,
            budgetMin = request.budgetMin,
            budgetMax = request.budgetMax,
            interests = request.interests
        )

    fun toUpdateCommand(request: UpdateGiftSearchRequestRequest): UpdateGiftSearchRequestCommand =
        UpdateGiftSearchRequestCommand(
            recipientDescription = request.recipientDescription,
            occasion = request.occasion,
            budgetMin = request.budgetMin,
            budgetMax = request.budgetMax,
            interests = request.interests,
            status = request.status
        )

    fun toResponse(model: GiftSearchRequestModel): GiftSearchRequestResponse =
        GiftSearchRequestResponse(
            id = model.id,
            conversationId = model.conversationId,
            userId = model.userId,
            rawPrompt = model.rawPrompt,
            recipientDescription = model.recipientDescription,
            occasion = model.occasion,
            budgetMin = model.budgetMin,
            budgetMax = model.budgetMax,
            interests = model.interests,
            status = model.status.name,
            createdAt = model.createdAt.toString(),
            updatedAt = model.updatedAt.toString()
        )
}