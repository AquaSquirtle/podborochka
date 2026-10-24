package org.podborochka.message.application.service.impl

import org.podborochka.common.exceptions.ResourceNotFoundException
import org.podborochka.conversation.infrastructure.repository.ConversationRepository
import org.podborochka.message.application.model.CreateMessageCommand
import org.podborochka.message.application.model.MessageModel
import org.podborochka.message.application.service.MessageService
import org.podborochka.message.infrastructure.mapper.MessagePersistenceMapper
import org.podborochka.message.infrastructure.repository.MessageRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MessageServiceImpl(
    private val messageRepository: MessageRepository,
    private val conversationRepository: ConversationRepository,
    private val mapper: MessagePersistenceMapper
) : MessageService {

    @Transactional
    override fun create(command: CreateMessageCommand): MessageModel {
        val conversation = conversationRepository.findByIdAndUserId(
            command.conversationId,
            command.userId
        ) ?: throw ResourceNotFoundException("Conversation not found")

        val entity = mapper.toNewEntity(
            conversation = conversation,
            role = command.role,
            content = command.content
        )

        val saved = messageRepository.save(entity)
        return mapper.toModel(saved)
    }

    override fun getAllByConversation(
        conversationId: Long,
        userId: Long,
        pageable: Pageable
    ): Page<MessageModel> {
        log.info(
            "Loading messages from DB: conversation={}, user={}, page={}, size={}",
            conversationId, userId, pageable.pageNumber, pageable.pageSize
        )
        conversationRepository.findByIdAndUserId(conversationId, userId)
            ?: throw ResourceNotFoundException("Conversation not found")

        return messageRepository.findAllByConversationIdOrderByCreatedAtAsc(conversationId, pageable)
            .map(mapper::toModel)
    }

    override fun getById(id: Long, conversationId: Long, userId: Long): MessageModel {
        val entity = messageRepository.findByIdAndConversationIdAndConversationUserId(
            id,
            conversationId,
            userId
        ) ?: throw ResourceNotFoundException("Message not found")

        return mapper.toModel(entity)
    }

    @Transactional
    override fun delete(id: Long, conversationId: Long, userId: Long) {
        val entity = messageRepository.findByIdAndConversationIdAndConversationUserId(
            id,
            conversationId,
            userId
        ) ?: throw ResourceNotFoundException("Message not found")

        messageRepository.delete(entity)
    }
    companion object {
        private val log = LoggerFactory.getLogger(MessageService::class.java)

    }
}