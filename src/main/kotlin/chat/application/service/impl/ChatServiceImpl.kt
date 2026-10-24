package org.podborochka.chat.application.service.impl

import org.podborochka.chat.application.model.ChatRecommendationItemModel
import org.podborochka.chat.application.model.ChatResultModel
import org.podborochka.chat.application.model.ProcessChatMessageCommand
import org.podborochka.chat.application.service.ChatService
import org.podborochka.chat.infrastructure.llm.LLMClient
import org.podborochka.common.exceptions.ResourceNotFoundException
import org.podborochka.conversation.infrastructure.repository.ConversationRepository
import org.podborochka.giftrecommendation.application.model.CreateGiftRecommendationCommand
import org.podborochka.giftrecommendation.application.service.GiftRecommendationService
import org.podborochka.giftsearchrequest.application.model.CreateGiftSearchRequestCommand
import org.podborochka.giftsearchrequest.application.model.UpdateGiftSearchRequestCommand
import org.podborochka.giftsearchrequest.application.service.GiftSearchRequestService
import org.podborochka.giftsearchrequest.domain.GiftSearchRequestStatus
import org.podborochka.message.application.model.CreateMessageCommand
import org.podborochka.message.application.service.MessageService
import org.podborochka.message.domain.MessageRole
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Service
@Transactional
class ChatServiceImpl(
    private val llmClient: LLMClient,
    private val conversationRepository: ConversationRepository,
    private val messageService: MessageService,
    private val giftSearchRequestService: GiftSearchRequestService,
    private val giftRecommendationService: GiftRecommendationService
) : ChatService {

    override fun processMessage(command: ProcessChatMessageCommand): ChatResultModel {
        val conversation = conversationRepository.findByIdAndUserId(
            command.conversationId,
            command.userId
        ) ?: throw ResourceNotFoundException("Conversation not found")

        messageService.create(
            CreateMessageCommand(
                userId = command.userId,
                conversationId = command.conversationId,
                role = MessageRole.USER,
                content = command.prompt
            )
        )

        val intent = llmClient.extractGiftIntent(command.prompt)

        val searchRequest = giftSearchRequestService.create(
            CreateGiftSearchRequestCommand(
                userId = command.userId,
                conversationId = command.conversationId,
                rawPrompt = command.prompt,
                recipientDescription = intent.recipientDescription,
                occasion = intent.occasion,
                budgetMin = intent.budgetMin?.toBigDecimal(),
                budgetMax = intent.budgetMax?.toBigDecimal(),
                interests = intent.interests.takeIf { it.isNotEmpty() }?.joinToString(", ")
            )
        )

        val llmResult = llmClient.generateGiftRecommendations(intent)

        val savedRecommendations = llmResult.recommendations.map { idea ->
            val saved = giftRecommendationService.create(
                CreateGiftRecommendationCommand(
                    userId = command.userId,
                    searchRequestId = searchRequest.id,
                    title = idea.title,
                    description = idea.description,
                    reasoning = idea.reasoning,
                    approximatePrice = idea.approximatePrice,
                    searchQuery = idea.searchQuery,
                    ozonSearchUrl = buildOzonSearchUrl(idea.searchQuery),
                    wildberriesSearchUrl = buildWildberriesSearchUrl(idea.searchQuery),
                    yandexMarketSearchUrl = buildYandexMarketSearchUrl(idea.searchQuery),
                    rankPosition = idea.rankPosition
                )
            )

            ChatRecommendationItemModel(
                id = saved.id,
                title = saved.title,
                description = saved.description,
                reasoning = saved.reasoning,
                approximatePrice = saved.approximatePrice,
                searchQuery = saved.searchQuery,
                rankPosition = saved.rankPosition,
                ozonSearchUrl = saved.ozonSearchUrl,
                wildberriesSearchUrl = saved.wildberriesSearchUrl,
                yandexMarketSearchUrl = saved.yandexMarketSearchUrl
            )
        }

        val assistantMessage = buildAssistantMessage(
            recipientSummary = llmResult.recipientSummary,
            recommendations = savedRecommendations
        )

        messageService.create(
            CreateMessageCommand(
                userId = command.userId,
                conversationId = command.conversationId,
                role = MessageRole.ASSISTANT,
                content = assistantMessage
            )
        )

        giftSearchRequestService.update(
            id = searchRequest.id,
            userId = command.userId,
            command = UpdateGiftSearchRequestCommand(
                status = GiftSearchRequestStatus.COMPLETED
            )
        )

        return ChatResultModel(
            conversationId = requireNotNull(conversation.id),
            prompt = command.prompt,
            assistantMessage = assistantMessage,
            recipientSummary = llmResult.recipientSummary,
            recommendations = savedRecommendations
        )
    }

    private fun buildAssistantMessage(
        recipientSummary: String?,
        recommendations: List<ChatRecommendationItemModel>
    ): String = buildString {
        appendLine(recipientSummary ?: "Вот несколько идей подарков:")
        appendLine()
        recommendations.forEach {
            appendLine("${it.rankPosition}. ${it.title}")
        }
    }

    private fun buildOzonSearchUrl(query: String): String =
        "https://www.ozon.ru/search/?text=${encode(query)}"

    private fun buildWildberriesSearchUrl(query: String): String =
        "https://www.wildberries.ru/catalog/0/search.aspx?search=${encode(query)}"

    private fun buildYandexMarketSearchUrl(query: String): String =
        "https://market.yandex.ru/search?text=${encode(query)}"

    private fun encode(value: String): String =
        URLEncoder.encode(value, StandardCharsets.UTF_8)
}