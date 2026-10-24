package org.podborochka.chat.infrastructure.llm.configuration
import com.google.genai.Client
import org.podborochka.common.properties.GeminiLLMProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GeminiClientConfiguration(
    private val geminiLLMProperties: GeminiLLMProperties,
) {
    @Bean
    fun geminiClient() =
        Client
            .builder()
            .apiKey(geminiLLMProperties.apiKey)
            .build()
}