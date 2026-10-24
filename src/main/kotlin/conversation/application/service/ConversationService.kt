package org.podborochka.conversation.application.service

import org.podborochka.conversation.application.model.ConversationModel
import org.podborochka.conversation.application.model.CreateConversationCommand
import org.podborochka.conversation.application.model.UpdateConversationCommand
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


interface ConversationService {

    fun create(command: CreateConversationCommand): ConversationModel

    fun getAllByUser(userId: Long, pageable: Pageable): Page<ConversationModel>

    fun getById(id: Long, userId: Long): ConversationModel

    fun update(id: Long, userId: Long, command: UpdateConversationCommand): ConversationModel

    fun delete(id: Long, userId: Long)
}