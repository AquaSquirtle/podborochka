package org.podborochka.product.infrastructure.repository

import org.podborochka.product.infrastructure.entity.ProductEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<ProductEntity, Long> {

    fun findAllByMarketplaceIgnoreCase(
        marketplace: String,
        pageable: Pageable
    ): Page<ProductEntity>
}