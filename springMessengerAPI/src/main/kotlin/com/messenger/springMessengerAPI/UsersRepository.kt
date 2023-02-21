package com.messenger.springMessengerAPI

import com.messenger.springMessengerAPI.models.Users
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UsersRepository : JpaRepository<Users, Int>