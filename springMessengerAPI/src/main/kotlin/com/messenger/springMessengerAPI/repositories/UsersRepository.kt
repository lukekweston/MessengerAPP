package com.messenger.springMessengerAPI.repositories

import com.messenger.springMessengerAPI.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UsersRepository : JpaRepository<User, Int>{
    fun findUsersByUsername(username: String): User
}