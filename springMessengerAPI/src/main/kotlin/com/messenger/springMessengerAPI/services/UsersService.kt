package com.messenger.springMessengerAPI.services

import com.messenger.springMessengerAPI.repositories.UsersRepository
import com.messenger.springMessengerAPI.models.User
import com.messenger.springMessengerAPI.models.request.UserLoginRequest
import com.messenger.springMessengerAPI.models.response.UserLoginResponse
import org.springframework.stereotype.Service


@Service
class UsersService(private val usersRepository: UsersRepository) {

    fun getAllUsers(): List<User> = usersRepository.findAll()
    fun getUserByUserName(userName: String): User = usersRepository.findUsersByUsername(username = userName)

    fun loginAndVerifyUser(userLoginRequest: UserLoginRequest): UserLoginResponse {
        val userId = usersRepository.findUsersByUsernameAndPassword(userLoginRequest.userName, userLoginRequest.password)?.id
        return UserLoginResponse(successfulLogin = userId != null, userId = userId)
    }
}