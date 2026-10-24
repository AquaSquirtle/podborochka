package org.podborochka.message.infrastructure.repository

import org.podborochka.message.infrastructure.entity.MessageEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepository : JpaRepository<MessageEntity, Long> {

    fun findAllByConversationIdOrderByCreatedAtAsc(
        conversationId: Long,
        pageable: Pageable
    ): Page<MessageEntity>

    fun findByIdAndConversationIdAndConversationUserId(
        id: Long,
        conversationId: Long,
        userId: Long
    ): MessageEntity?
}