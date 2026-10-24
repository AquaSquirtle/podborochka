package org.podborochka.chat.infrastructure.llm.impl
import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.Part
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component

@Component
class InstructionProvider(
    private val resourceLoader: ResourceLoader
) {
    fun extraction(): GenerateContentConfig? =
            GenerateContentConfig.builder()
                .systemInstruction(
                    Content.fromParts(
                        Part.fromText(
                        load(EXTRACT_INSTRUCTION_PATH)
                    ))
                )
                .build()
    fun recommend(): GenerateContentConfig? =
        GenerateContentConfig.builder()
            .systemInstruction(
                Content.fromParts(
                    Part.fromText(
                    load(SCORE_INSTRUCTION_PATH)
                ))
            )
            .build()



    private fun load(path: String): String {
        val resource = resourceLoader.getResource(path)

        require(resource.exists()) {
            "Resource not found: $path"
        }

        return resource.inputStream
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }
    }
    companion object {
        private const val EXTRACT_INSTRUCTION_PATH = "classpath:instructions/extract-gift-intent.txt"
        private const val SCORE_INSTRUCTION_PATH = "classpath:instructions/generate-gift-recommendations.txt"
    }
}