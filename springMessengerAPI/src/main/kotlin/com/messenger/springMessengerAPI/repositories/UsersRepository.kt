package com.messenger.springMessengerAPI.repositories

import com.messenger.springMessengerAPI.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UsersRepository : JpaRepository<User, Int>{
    fun findUsersByUsername(username: String): User

    //Login method with no auth, this is for local test purposes only - the whole login process is basic
    fun findUsersByUsernameAndPassword(username: String, password:String): User?
}