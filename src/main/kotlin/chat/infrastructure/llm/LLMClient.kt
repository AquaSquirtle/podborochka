package org.podborochka.chat.infrastructure.llm

import org.podborochka.chat.infrastructure.llm.dto.GiftIntentLlmResponse
import org.podborochka.chat.infrastructure.llm.dto.GiftRecommendationLlmResult

interface LLMClient {
    fun extractGiftIntent(userPrompt: String): GiftIntentLlmResponse
    fun generateGiftRecommendations(intent: GiftIntentLlmResponse): GiftRecommendationLlmResult
}