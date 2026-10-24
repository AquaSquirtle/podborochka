package org.podborochka.chat.infrastructure.llm.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.genai.Client
import org.podborochka.chat.infrastructure.llm.LLMClient
import org.podborochka.chat.infrastructure.llm.dto.GiftIntentLlmResponse
import org.podborochka.chat.infrastructure.llm.dto.GiftRecommendationLlmResult
import org.podborochka.common.exceptions.GeminiEmptyAnswerException
import org.podborochka.common.properties.GeminiLLMProperties
import org.springframework.stereotype.Component

@Component
class GeminiLLMClientImpl(
    private val geminiLLMProperties: GeminiLLMProperties,
    private val instructionProvider: InstructionProvider,
    private val geminiLLMClient: Client,
    private val objectMapper: ObjectMapper
) : LLMClient {

    override fun extractGiftIntent(userPrompt: String): GiftIntentLlmResponse {
        val response = geminiLLMClient.models.generateContent(
            geminiLLMProperties.model,
            userPrompt.trimIndent(),
            instructionProvider.extraction()
        ).text() ?: throw GeminiEmptyAnswerException("Gemini response not found")

        return objectMapper.readValue(cleanupJson(response), GiftIntentLlmResponse::class.java)
    }

    override fun generateGiftRecommendations(intent: GiftIntentLlmResponse): GiftRecommendationLlmResult {
        val response = geminiLLMClient.models.generateContent(
            geminiLLMProperties.model,
            buildRecommendationPayload(intent),
            instructionProvider.recommend()
        ).text() ?: throw GeminiEmptyAnswerException("Gemini recommendations not found")

        return objectMapper.readValue(cleanupJson(response), GiftRecommendationLlmResult::class.java)
    }

    private fun buildRecommendationPayload(intent: GiftIntentLlmResponse): String {
        val intentJson = objectMapper.writeValueAsString(intent)
        return """
            Критерии пользователя:
            $intentJson
        """.trimIndent()
    }

    private fun cleanupJson(text: String): String =
        text.removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
}