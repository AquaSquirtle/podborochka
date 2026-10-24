package org.podborochka.giftsearchrequest.application.model

import org.podborochka.giftsearchrequest.domain.GiftSearchRequestStatus
import java.math.BigDecimal

data class UpdateGiftSearchRequestCommand(
    val recipientDescription: String? = null,
    val occasion: String? = null,
    val budgetMin: BigDecimal? = null,
    val budgetMax: BigDecimal? = null,
    val interests: String? = null,
    val status: GiftSearchRequestStatus? = null
)