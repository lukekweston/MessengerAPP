package com.messenger.springMessengerAPI.repositories

import com.messenger.springMessengerAPI.models.Users
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UsersRepository : JpaRepository<Users, Int>{
    fun findUsersByUsername(username: String): Users
}