package org.podborochka.product.infrastructure.mapper

import org.podborochka.product.application.model.ProductModel
import org.podborochka.product.infrastructure.entity.ProductEntity
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class ProductPersistenceMapper {

    fun toModel(entity: ProductEntity): ProductModel =
        ProductModel(
            id = requireNotNull(entity.id),
            marketplace = entity.marketplace,
            title = entity.title,
            url = entity.url,
            imageUrl = entity.imageUrl,
            price = entity.price,
            currency = entity.currency,
            rating = entity.rating,
            createdAt = requireNotNull(entity.createdAt),
            updatedAt = requireNotNull(entity.updatedAt)
        )

    fun toNewEntity(
        marketplace: String,
        title: String,
        url: String,
        imageUrl: String?,
        price: BigDecimal?,
        currency: String?,
        rating: BigDecimal?
    ): ProductEntity =
        ProductEntity(
            marketplace = marketplace,
            title = title,
            url = url,
            imageUrl = imageUrl,
            price = price,
            currency = currency,
            rating = rating
        )
}