package com.messenger.springMessengerAPI.services

import com.messenger.springMessengerAPI.models.Message
import com.messenger.springMessengerAPI.repositories.MessageRepository
import org.springframework.stereotype.Service


@Service
class MessageService(private val messageRepository: MessageRepository) {
    fun getAllMessagesForConversation(conversationId: Int): List<Message> {
        return messageRepository.findAllByConversationId(conversationId)
    }
}