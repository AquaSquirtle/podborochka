package org.podborochka.chat.controller.dto

import jakarta.validation.constraints.NotBlank

data class SendChatMessageRequest(
    @NotBlank
    val prompt: String
)