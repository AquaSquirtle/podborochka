package org.podborochka.message.controller.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.podborochka.message.domain.MessageRole

data class CreateMessageRequest(

    @NotNull
    val role: MessageRole,

    @NotBlank
    val content: String
)