package org.podborochka.product.application.model

import java.math.BigDecimal

data class UpdateProductCommand(
    val marketplace: String? = null,
    val title: String? = null,
    val url: String? = null,
    val imageUrl: String? = null,
    val price: BigDecimal? = null,
    val currency: String? = null,
    val rating: BigDecimal? = null
)