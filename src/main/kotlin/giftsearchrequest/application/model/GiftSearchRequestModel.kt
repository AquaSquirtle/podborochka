package org.podborochka.giftsearchrequest.application.model

import org.podborochka.giftsearchrequest.domain.GiftSearchRequestStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class GiftSearchRequestModel(
    val id: Long,
    val conversationId: Long,
    val userId: Long,
    val rawPrompt: String,
    val recipientDescription: String?,
    val occasion: String?,
    val budgetMin: BigDecimal?,
    val budgetMax: BigDecimal?,
    val interests: String?,
    val status: GiftSearchRequestStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)