package org.podborochka.chat.controller.mapper

import org.podborochka.chat.application.model.ChatResultModel
import org.podborochka.chat.application.model.ProcessChatMessageCommand
import org.podborochka.chat.controller.dto.ChatRecommendationResponse
import org.podborochka.chat.controller.dto.SendChatMessageRequest
import org.podborochka.chat.controller.dto.SendChatMessageResponse
import org.springframework.stereotype.Component

@Component
class ChatControllerMapper {

    fun toCommand(
        userId: Long,
        conversationId: Long,
        request: SendChatMessageRequest
    ): ProcessChatMessageCommand =
        ProcessChatMessageCommand(
            userId = userId,
            conversationId = conversationId,
            prompt = request.prompt
        )

    fun toResponse(model: ChatResultModel): SendChatMessageResponse =
        SendChatMessageResponse(
            conversationId = model.conversationId,
            prompt = model.prompt,
            assistantMessage = model.assistantMessage,
            recipientSummary = model.recipientSummary,
            recommendations = model.recommendations.map {
                ChatRecommendationResponse(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    reasoning = it.reasoning,
                    approximatePrice = it.approximatePrice,
                    searchQuery = it.searchQuery,
                    rankPosition = it.rankPosition,
                    ozonSearchUrl = it.ozonSearchUrl,
                    wildberriesSearchUrl = it.wildberriesSearchUrl,
                    yandexMarketSearchUrl = it.yandexMarketSearchUrl
                )
            }
        )
}