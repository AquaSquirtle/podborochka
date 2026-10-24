package org.podborochka.message.application.service

import org.podborochka.message.application.model.CreateMessageCommand
import org.podborochka.message.application.model.MessageModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MessageService {

    fun create(command: CreateMessageCommand): MessageModel

    fun getAllByConversation(conversationId: Long, userId: Long, pageable: Pageable): Page<MessageModel>

    fun getById(id: Long, conversationId: Long, userId: Long): MessageModel

    fun delete(id: Long, conversationId: Long, userId: Long)
}