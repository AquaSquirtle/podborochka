package org.podborochka.product.application.service

import org.podborochka.product.application.model.CreateProductCommand
import org.podborochka.product.application.model.ProductModel
import org.podborochka.product.application.model.UpdateProductCommand
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductService {

    fun create(command: CreateProductCommand): ProductModel

    fun getAll(pageable: Pageable): Page<ProductModel>

    fun getByMarketplace(marketplace: String, pageable: Pageable): Page<ProductModel>

    fun getById(id: Long): ProductModel

    fun update(id: Long, command: UpdateProductCommand): ProductModel

    fun delete(id: Long)
}