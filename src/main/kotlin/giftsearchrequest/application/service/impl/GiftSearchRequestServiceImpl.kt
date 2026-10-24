package org.podborochka.giftsearchrequest.application.service.impl

import org.podborochka.common.exceptions.ResourceNotFoundException
import org.podborochka.conversation.infrastructure.repository.ConversationRepository
import org.podborochka.giftsearchrequest.application.model.CreateGiftSearchRequestCommand
import org.podborochka.giftsearchrequest.application.model.GiftSearchRequestModel
import org.podborochka.giftsearchrequest.application.model.UpdateGiftSearchRequestCommand
import org.podborochka.giftsearchrequest.application.service.GiftSearchRequestService
import org.podborochka.giftsearchrequest.infrastructure.mapper.GiftSearchRequestPersistenceMapper
import org.podborochka.giftsearchrequest.infrastructure.repository.GiftSearchRequestRepository
import org.podborochka.user.infrastructure.repository.UserRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional(readOnly = true)
class GiftSearchRequestServiceImpl(
    private val giftSearchRequestRepository: GiftSearchRequestRepository,
    private val conversationRepository: ConversationRepository,
    private val userRepository: UserRepository,
    private val mapper: GiftSearchRequestPersistenceMapper
) : GiftSearchRequestService {

    @CacheEvict(value = ["messagesByConversation"], allEntries = true)
    @Transactional
    override fun create(command: CreateGiftSearchRequestCommand): GiftSearchRequestModel {
        val user = userRepository.findById(command.userId)
            .orElseThrow { ResourceNotFoundException("User not found") }

        val conversation = conversationRepository.findByIdAndUserId(
            command.conversationId,
            command.userId
        ) ?: throw ResourceNotFoundException("Conversation not found")

        val entity = mapper.toNewEntity(
            conversation = conversation,
            user = user,
            rawPrompt = command.rawPrompt,
            recipientDescription = command.recipientDescription,
            occasion = command.occasion,
            budgetMin = command.budgetMin,
            budgetMax = command.budgetMax,
            interests = command.interests
        )

        val saved = giftSearchRequestRepository.save(entity)
        return mapper.toModel(saved)
    }

    override fun getAllByUser(userId: Long, pageable: Pageable): Page<GiftSearchRequestModel> =
        giftSearchRequestRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable)
            .map(mapper::toModel)

    @Cacheable(
        value = ["messagesByConversation"],
        key = "#conversationId + ':' + #userId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize"
    )
    override fun getAllByConversation(
        conversationId: Long,
        userId: Long,
        pageable: Pageable
    ): Page<GiftSearchRequestModel> {
        conversationRepository.findByIdAndUserId(conversationId, userId)
            ?: throw ResourceNotFoundException("Conversation not found")

        return giftSearchRequestRepository
            .findAllByConversationIdAndUserIdOrderByCreatedAtDesc(conversationId, userId, pageable)
            .map(mapper::toModel)
    }

    override fun getById(id: Long, userId: Long): GiftSearchRequestModel {
        val entity = giftSearchRequestRepository.findByIdAndUserId(id, userId)
            ?: throw ResourceNotFoundException("Gift search request not found")

        return mapper.toModel(entity)
    }

    @Transactional
    override fun update(
        id: Long,
        userId: Long,
        command: UpdateGiftSearchRequestCommand
    ): GiftSearchRequestModel {
        val entity = giftSearchRequestRepository.findByIdAndUserId(id, userId)
            ?: throw ResourceNotFoundException("Gift search request not found")

        command.recipientDescription?.let { entity.recipientDescription = it }
        command.occasion?.let { entity.occasion = it }
        command.budgetMin?.let { entity.budgetMin = it }
        command.budgetMax?.let { entity.budgetMax = it }
        command.interests?.let { entity.interests = it }
        command.status?.let { entity.status = it }

        val saved = giftSearchRequestRepository.save(entity)
        return mapper.toModel(saved)
    }

    @CacheEvict(value = ["messagesByConversation"], allEntries = true)
    @Transactional
    override fun delete(id: Long, userId: Long) {
        val entity = giftSearchRequestRepository.findByIdAndUserId(id, userId)
            ?: throw ResourceNotFoundException("Gift search request not found")

        giftSearchRequestRepository.delete(entity)
    }
}