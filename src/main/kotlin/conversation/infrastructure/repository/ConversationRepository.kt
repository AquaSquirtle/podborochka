package org.podborochka.conversation.infrastructure.repository

import org.podborochka.conversation.infrastructure.entity.ConversationEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ConversationRepository : JpaRepository<ConversationEntity, Long> {

    fun findAllByUserId(userId: Long, pageable: Pageable): Page<ConversationEntity>

    fun findByIdAndUserId(id: Long, userId: Long): ConversationEntity?
}