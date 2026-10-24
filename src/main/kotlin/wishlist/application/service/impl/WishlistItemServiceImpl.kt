package org.podborochka.wishlist.application.service.impl
import org.podborochka.common.exceptions.ResourceNotFoundException
import org.podborochka.giftrecommendation.infrastructure.repository.GiftRecommendationRepository
import org.podborochka.product.infrastructure.repository.ProductRepository
import org.podborochka.user.infrastructure.repository.UserRepository
import org.podborochka.wishlist.application.model.CreateWishlistItemCommand
import org.podborochka.wishlist.application.model.UpdateWishlistItemCommand
import org.podborochka.wishlist.application.model.WishlistItemModel
import org.podborochka.wishlist.application.service.WishlistItemService
import org.podborochka.wishlist.application.service.WishlistNotificationService
import org.podborochka.wishlist.infrastructure.mapper.WishlistPersistenceMapper
import org.podborochka.wishlist.infrastructure.repository.WishlistItemRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class WishlistItemServiceImpl(
    private val repository: WishlistItemRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val recommendationRepository: GiftRecommendationRepository,
    private val mapper: WishlistPersistenceMapper,
    private val wishlistNotificationService: WishlistNotificationService
) : WishlistItemService {

    @Transactional
    override fun create(command: CreateWishlistItemCommand): WishlistItemModel {
        validate(command)

        val user = userRepository.findById(command.userId)
            .orElseThrow { ResourceNotFoundException("User not found") }

        val product = command.productId?.let {
            productRepository.findById(it)
                .orElseThrow { ResourceNotFoundException("Product not found") }
        }

        val recommendation = command.recommendationId?.let {
            recommendationRepository.findById(it)
                .orElseThrow { ResourceNotFoundException("Recommendation not found") }
        }

        val entity = mapper.toNewEntity(
            user,
            product,
            recommendation,
            command.customTitle,
            command.customUrl,
            command.notes
        )

        val saved = repository.save(entity)
        wishlistNotificationService.notifyWishlistSaved(
            user.id!!,
            saved.customTitle ?: "Подарок"
        )
        return mapper.toModel(saved)
    }

    override fun getAll(userId: Long, pageable: Pageable): Page<WishlistItemModel> =
        repository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable)
            .map(mapper::toModel)

    override fun getById(id: Long, userId: Long): WishlistItemModel =
        mapper.toModel(
            repository.findByIdAndUserId(id, userId)
                ?: throw ResourceNotFoundException("Wishlist item not found")
        )

    @Transactional
    override fun update(id: Long, userId: Long, command: UpdateWishlistItemCommand): WishlistItemModel {
        val entity = repository.findByIdAndUserId(id, userId)
            ?: throw ResourceNotFoundException("Wishlist item not found")

        command.isPurchased?.let { entity.isPurchased = it }
        command.customTitle?.let { entity.customTitle = it }
        command.customUrl?.let { entity.customUrl = it }
        command.notes?.let { entity.notes = it }

        return mapper.toModel(repository.save(entity))
    }

    @Transactional
    override fun delete(id: Long, userId: Long) {
        val entity = repository.findByIdAndUserId(id, userId)
            ?: throw ResourceNotFoundException("Wishlist item not found")

        repository.delete(entity)
    }

    private fun validate(command: CreateWishlistItemCommand) {
        if (
            command.productId == null &&
            command.recommendationId == null &&
            command.customTitle.isNullOrBlank()
        ) {
            throw IllegalArgumentException("Wishlist item must have at least one source")
        }
    }
}