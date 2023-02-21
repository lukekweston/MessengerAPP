package com.messenger.springMessengerAPI.models

import jakarta.persistence.*


@Entity
class Users(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "UserName", unique = true, nullable = false)
    val username: String = "",

    @Column(name = "UserEmail", unique = true, nullable = false)
    val useremail: String = "",

    @Column(name = "Password", nullable = false)
    val password: String = "",

)