package org.podborochka.giftsearchrequest.controller.dto

import java.math.BigDecimal

data class GiftSearchRequestResponse(
    val id: Long,
    val conversationId: Long,
    val userId: Long,
    val rawPrompt: String,
    val recipientDescription: String?,
    val occasion: String?,
    val budgetMin: BigDecimal?,
    val budgetMax: BigDecimal?,
    val interests: String?,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)