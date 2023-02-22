package com.messenger.springMessengerAPI.services

import com.messenger.springMessengerAPI.repositories.UsersRepository
import com.messenger.springMessengerAPI.models.Users
import org.springframework.stereotype.Service


@Service
class UsersService(private val usersRepository: UsersRepository) {

    fun getAllUsers() : List<Users> = usersRepository.findAll()
    fun getUserByUserName(userName: String) : Users = usersRepository.findUsersByUsername(username = userName)
}