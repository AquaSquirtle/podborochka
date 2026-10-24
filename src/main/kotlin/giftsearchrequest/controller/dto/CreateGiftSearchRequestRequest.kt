package org.podborochka.giftsearchrequest.controller.dto

import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

data class CreateGiftSearchRequestRequest(
    @NotBlank
    val rawPrompt: String,
    val recipientDescription: String? = null,
    val occasion: String? = null,
    val budgetMin: BigDecimal? = null,
    val budgetMax: BigDecimal? = null,
    val interests: String? = null
)