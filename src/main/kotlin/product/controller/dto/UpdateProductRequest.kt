package org.podborochka.product.controller.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class UpdateProductRequest(
    @Size(max = 100)
    val marketplace: String? = null,

    @Size(max = 255)
    val title: String? = null,

    val url: String? = null,

    val imageUrl: String? = null,

    @DecimalMin(value = "0.0", inclusive = true)
    val price: BigDecimal? = null,

    @Size(max = 10)
    val currency: String? = null,

    @DecimalMin(value = "0.0", inclusive = true)
    val rating: BigDecimal? = null
)