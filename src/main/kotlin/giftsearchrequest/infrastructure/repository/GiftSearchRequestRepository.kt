package org.podborochka.giftsearchrequest.infrastructure.repository

import org.podborochka.giftsearchrequest.infrastructure.entity.GiftSearchRequestEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface GiftSearchRequestRepository : JpaRepository<GiftSearchRequestEntity, Long> {

    fun findAllByUserIdOrderByCreatedAtDesc(
        userId: Long,
        pageable: Pageable
    ): Page<GiftSearchRequestEntity>

    fun findAllByConversationIdAndUserIdOrderByCreatedAtDesc(
        conversationId: Long,
        userId: Long,
        pageable: Pageable
    ): Page<GiftSearchRequestEntity>

    fun findByIdAndUserId(id: Long, userId: Long): GiftSearchRequestEntity?
}