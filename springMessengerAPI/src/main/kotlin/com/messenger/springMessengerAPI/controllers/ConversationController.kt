package com.messenger.springMessengerAPI.controllers

import com.messenger.springMessengerAPI.services.ConversationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
class ConversationController(private val conversationService: ConversationService) {
    @GetMapping("/getConversationNameById/{Id}")
    fun findByUserName(@PathVariable Id: Int): String? =
        conversationService.getConversationNameById(Id = Id)

}