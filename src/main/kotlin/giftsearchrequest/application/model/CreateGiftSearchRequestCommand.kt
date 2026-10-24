package org.podborochka.giftsearchrequest.application.model

import java.math.BigDecimal

data class CreateGiftSearchRequestCommand(
    val userId: Long,
    val conversationId: Long,
    val rawPrompt: String,
    val recipientDescription: String? = null,
    val occasion: String? = null,
    val budgetMin: BigDecimal? = null,
    val budgetMax: BigDecimal? = null,
    val interests: String? = null
)