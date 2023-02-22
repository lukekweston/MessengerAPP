package com.messenger.springMessengerAPI.services

import com.messenger.springMessengerAPI.repositories.ConversationRepository
import org.springframework.stereotype.Service


@Service
class ConversationService(private val conversationRepository: ConversationRepository) {

    fun getConversationNameById(Id: Int): String? {
        return conversationRepository.findAllById(Id).conversationName
    }
}