package org.podborochka.giftrecommendation.application.service.impl

import org.podborochka.common.exceptions.DuplicateResourceException
import org.podborochka.common.exceptions.ResourceNotFoundException
import org.podborochka.giftrecommendation.application.model.CreateGiftRecommendationCommand
import org.podborochka.giftrecommendation.application.model.GiftRecommendationModel
import org.podborochka.giftrecommendation.application.model.UpdateGiftRecommendationCommand
import org.podborochka.giftrecommendation.application.service.GiftRecommendationService
import org.podborochka.giftrecommendation.infrastructure.mapper.GiftRecommendationPersistenceMapper
import org.podborochka.giftrecommendation.infrastructure.repository.GiftRecommendationRepository
import org.podborochka.giftsearchrequest.infrastructure.repository.GiftSearchRequestRepository
import org.podborochka.product.infrastructure.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GiftRecommendationServiceImpl(
    private val giftRecommendationRepository: GiftRecommendationRepository,
    private val giftSearchRequestRepository: GiftSearchRequestRepository,
    private val productRepository: ProductRepository,
    private val mapper: GiftRecommendationPersistenceMapper
) : GiftRecommendationService {

    @Transactional
    override fun create(command: CreateGiftRecommendationCommand): GiftRecommendationModel {
        validateRankPosition(command.rankPosition)

        val searchRequest = giftSearchRequestRepository.findByIdAndUserId(
            command.searchRequestId,
            command.userId
        ) ?: throw ResourceNotFoundException("Gift search request not found")

        if (giftRecommendationRepository.existsBySearchRequestIdAndRankPosition(
                command.searchRequestId,
                command.rankPosition
            )
        ) {
            throw DuplicateResourceException(
                "Recommendation with rank ${command.rankPosition} already exists for this search request"
            )
        }

        val product = command.productId?.let { productId ->
            productRepository.findById(productId)
                .orElseThrow { ResourceNotFoundException("Product not found") }
        }

        val entity = mapper.toNewEntity(
            searchRequest = searchRequest,
            product = product,
            title = command.title,
            description = command.description,
            reasoning = command.reasoning,
            approximatePrice = command.approximatePrice,
            searchQuery = command.searchQuery,
            ozonSearchUrl = command.ozonSearchUrl,
            wildberriesSearchUrl = command.wildberriesSearchUrl,
            yandexMarketSearchUrl = command.yandexMarketSearchUrl,
            rankPosition = command.rankPosition
        )

        val saved = giftRecommendationRepository.save(entity)
        return mapper.toModel(saved)
    }

    override fun getAllBySearchRequest(
        searchRequestId: Long,
        userId: Long,
        pageable: Pageable
    ): Page<GiftRecommendationModel> {
        giftSearchRequestRepository.findByIdAndUserId(searchRequestId, userId)
            ?: throw ResourceNotFoundException("Gift search request not found")

        return giftRecommendationRepository
            .findAllBySearchRequestIdAndSearchRequestUserIdOrderByRankPositionAsc(searchRequestId, userId, pageable)
            .map(mapper::toModel)
    }

    override fun getById(id: Long, userId: Long): GiftRecommendationModel {
        val entity = giftRecommendationRepository.findByIdAndSearchRequestUserId(id, userId)
            ?: throw ResourceNotFoundException("Gift recommendation not found")

        return mapper.toModel(entity)
    }

    @Transactional
    override fun update(
        id: Long,
        userId: Long,
        command: UpdateGiftRecommendationCommand
    ): GiftRecommendationModel {
        val entity = giftRecommendationRepository.findByIdAndSearchRequestUserId(id, userId)
            ?: throw ResourceNotFoundException("Gift recommendation not found")

        val targetSearchRequestId = requireNotNull(entity.searchRequest.id)

        command.rankPosition?.let { newRank ->
            validateRankPosition(newRank)

            if (newRank != entity.rankPosition &&
                giftRecommendationRepository.existsBySearchRequestIdAndRankPosition(targetSearchRequestId, newRank)
            ) {
                throw DuplicateResourceException(
                    "Recommendation with rank $newRank already exists for this search request"
                )
            }

            entity.rankPosition = newRank
        }

        if (command.productId != null) {
            val product = productRepository.findById(command.productId)
                .orElseThrow { ResourceNotFoundException("Product not found") }
            entity.product = product
        }

        command.title?.let { entity.title = it }
        command.description?.let { entity.description = it }
        command.reasoning?.let { entity.reasoning = it }
        command.approximatePrice?.let { entity.approximatePrice = it }
        command.searchQuery?.let { entity.searchQuery = it }
        command.ozonSearchUrl?.let { entity.ozonSearchUrl = it }
        command.wildberriesSearchUrl?.let { entity.wildberriesSearchUrl = it }
        command.yandexMarketSearchUrl?.let { entity.yandexMarketSearchUrl = it }

        val saved = giftRecommendationRepository.save(entity)
        return mapper.toModel(saved)
    }

    @Transactional
    override fun delete(id: Long, userId: Long) {
        val entity = giftRecommendationRepository.findByIdAndSearchRequestUserId(id, userId)
            ?: throw ResourceNotFoundException("Gift recommendation not found")

        giftRecommendationRepository.delete(entity)
    }

    private fun validateRankPosition(rankPosition: Int) {
        if (rankPosition <= 0) {
            throw IllegalArgumentException("rankPosition must be greater than 0")
        }
    }
}
