package com.messenger.springMessengerAPI.controllers

import com.messenger.springMessengerAPI.models.Message
import com.messenger.springMessengerAPI.services.ConversationService
import com.messenger.springMessengerAPI.services.MessageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
class MessageController(private val messageService: MessageService) {
    @GetMapping("/getAllMessagesForConversation/{conversationId}")
    fun getAllMessagesForConversation(@PathVariable conversationId: Int): List<Message> =
            messageService.getAllMessagesForConversation(conversationId = conversationId)


    //Todo - get all messages for a user

    //Todo - get all messages after a dateTime

}