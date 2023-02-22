package com.messenger.springMessengerAPI.controllers

import com.messenger.springMessengerAPI.models.Users
import com.messenger.springMessengerAPI.services.UserConversationService
import com.messenger.springMessengerAPI.services.UsersService
import org.springframework.data.repository.query.Param
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
class UserConversationController(private val userConversationService: UserConversationService) {

    @GetMapping("/findOtherConversationUsers/{userId}")
    fun findByUserName(@PathVariable userId: Int): List<Int> =
            userConversationService.findAllOtherUserIdsForConversation(userId)

}