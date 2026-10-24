package org.podborochka.product.controller.mapper

import org.podborochka.product.application.model.CreateProductCommand
import org.podborochka.product.application.model.ProductModel
import org.podborochka.product.application.model.UpdateProductCommand
import org.podborochka.product.controller.dto.CreateProductRequest
import org.podborochka.product.controller.dto.ProductResponse
import org.podborochka.product.controller.dto.UpdateProductRequest
import org.springframework.stereotype.Component

@Component
class ProductControllerMapper {

    fun toCreateCommand(request: CreateProductRequest): CreateProductCommand =
        CreateProductCommand(
            marketplace = request.marketplace,
            title = request.title,
            url = request.url,
            imageUrl = request.imageUrl,
            price = request.price,
            currency = request.currency,
            rating = request.rating
        )

    fun toUpdateCommand(request: UpdateProductRequest): UpdateProductCommand =
        UpdateProductCommand(
            marketplace = request.marketplace,
            title = request.title,
            url = request.url,
            imageUrl = request.imageUrl,
            price = request.price,
            currency = request.currency,
            rating = request.rating
        )

    fun toResponse(model: ProductModel): ProductResponse =
        ProductResponse(
            id = model.id,
            marketplace = model.marketplace,
            title = model.title,
            url = model.url,
            imageUrl = model.imageUrl,
            price = model.price,
            currency = model.currency,
            rating = model.rating,
            createdAt = model.createdAt.toString(),
            updatedAt = model.updatedAt.toString()
        )
}