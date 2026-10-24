package org.podborochka.giftrecommendation.infrastructure.repository

import org.podborochka.giftrecommendation.infrastructure.entity.GiftRecommendationEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface GiftRecommendationRepository : JpaRepository<GiftRecommendationEntity, Long> {

    fun findAllBySearchRequestIdAndSearchRequestUserIdOrderByRankPositionAsc(
        searchRequestId: Long,
        userId: Long,
        pageable: Pageable
    ): Page<GiftRecommendationEntity>

    fun findByIdAndSearchRequestUserId(
        id: Long,
        userId: Long
    ): GiftRecommendationEntity?

    fun existsBySearchRequestIdAndRankPosition(
        searchRequestId: Long,
        rankPosition: Int
    ): Boolean
}