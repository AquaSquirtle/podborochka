package org.podborochka.product.application.model

import java.math.BigDecimal

data class CreateProductCommand(
    val marketplace: String,
    val title: String,
    val url: String,
    val imageUrl: String? = null,
    val price: BigDecimal? = null,
    val currency: String? = null,
    val rating: BigDecimal? = null
)