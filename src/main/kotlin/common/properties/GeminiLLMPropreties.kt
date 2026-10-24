package org.podborochka.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "gemini")
data class GeminiLLMProperties(
    val apiKey: String,
    val model: String
)