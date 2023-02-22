package com.messenger.springMessengerAPI.controllers

import com.messenger.springMessengerAPI.models.User
import com.messenger.springMessengerAPI.services.UsersService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
class UsersController(private val usersService: UsersService) {

    @GetMapping("/userlist")
    fun getAllUsers(): List<User> = usersService.getAllUsers()

    @GetMapping("/findUser/{userName}")
    fun findByUserName(@PathVariable userName: String): User =
        usersService.getUserByUserName(userName = userName)

}