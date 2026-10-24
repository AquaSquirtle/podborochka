package org.podborochka.conversation.application.service.impl

import org.podborochka.common.exceptions.ResourceNotFoundException
import org.podborochka.conversation.application.model.ConversationModel
import org.podborochka.conversation.application.model.CreateConversationCommand
import org.podborochka.conversation.application.model.UpdateConversationCommand
import org.podborochka.conversation.application.service.ConversationService
import org.podborochka.conversation.infrastructure.mapper.ConversationPersistenceMapper
import org.podborochka.conversation.infrastructure.repository.ConversationRepository
import org.podborochka.user.infrastructure.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional(readOnly = true)
class ConversationServiceImpl(
    private val conversationRepository: ConversationRepository,
    private val userRepository: UserRepository,
    private val mapper: ConversationPersistenceMapper
) : ConversationService {

    @Transactional
    override fun create(command: CreateConversationCommand): ConversationModel {
        val user = userRepository.findById(command.userId)
            .orElseThrow { ResourceNotFoundException("User not found") }

        val entity = mapper.toNewEntity(user, command.title)
        val saved = conversationRepository.save(entity)

        return mapper.toModel(saved)
    }

    override fun getAllByUser(userId: Long, pageable: Pageable): Page<ConversationModel> =
        conversationRepository.findAllByUserId(userId, pageable)
            .map(mapper::toModel)

    override fun getById(id: Long, userId: Long): ConversationModel {
        val entity = conversationRepository.findByIdAndUserId(id, userId)
            ?: throw ResourceNotFoundException("Conversation not found")

        return mapper.toModel(entity)
    }

    @Transactional
    override fun update(id: Long, userId: Long, command: UpdateConversationCommand): ConversationModel {
        val entity = conversationRepository.findByIdAndUserId(id, userId)
            ?: throw ResourceNotFoundException("Conversation not found")

        command.title?.let { entity.title = it }

        val saved = conversationRepository.save(entity)
        return mapper.toModel(saved)
    }

    @Transactional
    override fun delete(id: Long, userId: Long) {
        val entity = conversationRepository.findByIdAndUserId(id, userId)
            ?: throw ResourceNotFoundException("Conversation not found")

        conversationRepository.delete(entity)
    }
}