package org.podborochka.product.controller.dto

import java.math.BigDecimal

data class ProductResponse(
    val id: Long,
    val marketplace: String,
    val title: String,
    val url: String,
    val imageUrl: String?,
    val price: BigDecimal?,
    val currency: String?,
    val rating: BigDecimal?,
    val createdAt: String,
    val updatedAt: String
)