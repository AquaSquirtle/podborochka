package org.podborochka.product.controller.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class CreateProductRequest(

    @NotBlank
    @Size(max = 100)
    val marketplace: String,

    @NotBlank
    @Size(max = 255)
    val title: String,

    @NotBlank
    val url: String,

    val imageUrl: String? = null,

    @DecimalMin(value = "0.0", inclusive = true)
    val price: BigDecimal? = null,

    @Size(max = 10)
    val currency: String? = null,

    @DecimalMin(value = "0.0", inclusive = true)
    val rating: BigDecimal? = null
)