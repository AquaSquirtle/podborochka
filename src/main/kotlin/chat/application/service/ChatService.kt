package org.podborochka.chat.application.service

import org.podborochka.chat.application.model.ChatResultModel
import org.podborochka.chat.application.model.ProcessChatMessageCommand

interface ChatService {
    fun processMessage(command: ProcessChatMessageCommand): ChatResultModel
}