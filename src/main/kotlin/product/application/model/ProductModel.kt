package org.podborochka.product.application.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductModel(
    val id: Long,
    val marketplace: String,
    val title: String,
    val url: String,
    val imageUrl: String?,
    val price: BigDecimal?,
    val currency: String?,
    val rating: BigDecimal?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)