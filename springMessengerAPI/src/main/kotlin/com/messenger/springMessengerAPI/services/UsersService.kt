package com.messenger.springMessengerAPI.services

import com.messenger.springMessengerAPI.repositories.UsersRepository
import com.messenger.springMessengerAPI.models.User
import org.springframework.stereotype.Service


@Service
class UsersService(private val usersRepository: UsersRepository) {

    fun getAllUsers() : List<User> = usersRepository.findAll()
    fun getUserByUserName(userName: String) : User = usersRepository.findUsersByUsername(username = userName)
}