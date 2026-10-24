package org.podborochka.giftsearchrequest.controller.dto

import org.podborochka.giftsearchrequest.domain.GiftSearchRequestStatus
import java.math.BigDecimal

data class UpdateGiftSearchRequestRequest(
    val recipientDescription: String? = null,
    val occasion: String? = null,
    val budgetMin: BigDecimal? = null,
    val budgetMax: BigDecimal? = null,
    val interests: String? = null,
    val status: GiftSearchRequestStatus? = null
)