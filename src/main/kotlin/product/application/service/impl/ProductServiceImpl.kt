package org.podborochka.product.application.service.impl

import org.podborochka.common.exceptions.ResourceNotFoundException
import org.podborochka.product.application.model.CreateProductCommand
import org.podborochka.product.application.model.ProductModel
import org.podborochka.product.application.model.UpdateProductCommand
import org.podborochka.product.application.service.ProductService
import org.podborochka.product.infrastructure.mapper.ProductPersistenceMapper
import org.podborochka.product.infrastructure.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional(readOnly = true)
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val mapper: ProductPersistenceMapper
) : ProductService {

    @Transactional
    override fun create(command: CreateProductCommand): ProductModel {
        command.price?.let { validatePrice(it) }
        command.rating?.let { validateRating(it) }

        val entity = mapper.toNewEntity(
            marketplace = command.marketplace,
            title = command.title,
            url = command.url,
            imageUrl = command.imageUrl,
            price = command.price,
            currency = command.currency,
            rating = command.rating
        )

        val saved = productRepository.save(entity)
        return mapper.toModel(saved)
    }

    override fun getAll(pageable: Pageable): Page<ProductModel> =
        productRepository.findAll(pageable).map(mapper::toModel)

    override fun getByMarketplace(marketplace: String, pageable: Pageable): Page<ProductModel> =
        productRepository.findAllByMarketplaceIgnoreCase(marketplace, pageable)
            .map(mapper::toModel)

    override fun getById(id: Long): ProductModel {
        val entity = productRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product not found") }

        return mapper.toModel(entity)
    }

    @Transactional
    override fun update(id: Long, command: UpdateProductCommand): ProductModel {
        val entity = productRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product not found") }

        command.price?.let { validatePrice(it) }
        command.rating?.let { validateRating(it) }

        command.marketplace?.let { entity.marketplace = it }
        command.title?.let { entity.title = it }
        command.url?.let { entity.url = it }
        command.imageUrl?.let { entity.imageUrl = it }
        command.price?.let { entity.price = it }
        command.currency?.let { entity.currency = it }
        command.rating?.let { entity.rating = it }

        val saved = productRepository.save(entity)
        return mapper.toModel(saved)
    }

    @Transactional
    override fun delete(id: Long) {
        val entity = productRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product not found") }

        productRepository.delete(entity)
    }

    private fun validatePrice(price: BigDecimal) {
        if (price < BigDecimal.ZERO) {
            throw IllegalArgumentException("price cannot be negative")
        }
    }

    private fun validateRating(rating: BigDecimal) {
        if (rating < BigDecimal.ZERO || rating > BigDecimal("5.00")) {
            throw IllegalArgumentException("rating must be between 0 and 5")
        }
    }
}