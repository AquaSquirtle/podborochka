package org.podborochka.product.controller

import org.podborochka.common.web.utils.PaginationUtils
import org.podborochka.graphql.dto.CreateProductInputGql
import org.podborochka.graphql.dto.ProductPageGql
import org.podborochka.graphql.dto.UpdateProductInputGql
import org.podborochka.product.application.service.ProductService
import org.podborochka.product.controller.dto.CreateProductRequest
import org.podborochka.product.controller.dto.UpdateProductRequest
import org.podborochka.product.controller.mapper.ProductControllerMapper
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import java.math.BigDecimal

@Controller
class ProductGraphQlController(
    private val productService: ProductService,
    private val productControllerMapper: ProductControllerMapper
) {

    @QueryMapping
    fun products(
        @Argument page: Int?,
        @Argument size: Int?
    ): ProductPageGql {
        val pageable = PaginationUtils.pageable(page, size)
        val result = productService.getAll(pageable)

        return ProductPageGql(
            content = result.content.map(productControllerMapper::toResponse),
            page = result.number,
            size = result.size,
            totalElements = result.totalElements.toInt(),
            totalPages = result.totalPages
        )
    }

    @QueryMapping
    fun product(@Argument id: Long) =
        productControllerMapper.toResponse(productService.getById(id))

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createProduct(@Argument input: CreateProductInputGql) =
        productControllerMapper.toResponse(
            productService.create(
                productControllerMapper.toCreateCommand(
                    CreateProductRequest(
                        marketplace = input.marketplace,
                        title = input.title,
                        url = input.url,
                        imageUrl = input.imageUrl,
                        price = input.price?.let(::toBigDecimal),
                        currency = input.currency,
                        rating = input.rating?.let(::toBigDecimal)
                    )
                )
            )
        )

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun updateProduct(
        @Argument id: Long,
        @Argument input: UpdateProductInputGql
    ) = productControllerMapper.toResponse(
        productService.update(
            id,
            productControllerMapper.toUpdateCommand(
                UpdateProductRequest(
                    marketplace = input.marketplace,
                    title = input.title,
                    url = input.url,
                    imageUrl = input.imageUrl,
                    price = input.price?.let(::toBigDecimal),
                    currency = input.currency,
                    rating = input.rating?.let(::toBigDecimal)
                )
            )
        )
    )

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteProduct(@Argument id: Long): Boolean {
        productService.delete(id)
        return true
    }

    private fun toBigDecimal(value: Double): BigDecimal =
        BigDecimal.valueOf(value)
}