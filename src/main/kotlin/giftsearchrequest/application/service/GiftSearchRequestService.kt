package org.podborochka.giftsearchrequest.application.service

import org.podborochka.giftsearchrequest.application.model.CreateGiftSearchRequestCommand
import org.podborochka.giftsearchrequest.application.model.GiftSearchRequestModel
import org.podborochka.giftsearchrequest.application.model.UpdateGiftSearchRequestCommand
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GiftSearchRequestService {

    fun create(command: CreateGiftSearchRequestCommand): GiftSearchRequestModel

    fun getAllByUser(userId: Long, pageable: Pageable): Page<GiftSearchRequestModel>

    fun getAllByConversation(conversationId: Long, userId: Long, pageable: Pageable): Page<GiftSearchRequestModel>

    fun getById(id: Long, userId: Long): GiftSearchRequestModel

    fun update(id: Long, userId: Long, command: UpdateGiftSearchRequestCommand): GiftSearchRequestModel

    fun delete(id: Long, userId: Long)
}