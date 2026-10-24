package org.podborochka.giftrecommendation.application.service

import org.podborochka.giftrecommendation.application.model.CreateGiftRecommendationCommand
import org.podborochka.giftrecommendation.application.model.GiftRecommendationModel
import org.podborochka.giftrecommendation.application.model.UpdateGiftRecommendationCommand
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GiftRecommendationService {

    fun create(command: CreateGiftRecommendationCommand): GiftRecommendationModel

    fun getAllBySearchRequest(searchRequestId: Long, userId: Long, pageable: Pageable): Page<GiftRecommendationModel>

    fun getById(id: Long, userId: Long): GiftRecommendationModel

    fun update(id: Long, userId: Long, command: UpdateGiftRecommendationCommand): GiftRecommendationModel

    fun delete(id: Long, userId: Long)
}