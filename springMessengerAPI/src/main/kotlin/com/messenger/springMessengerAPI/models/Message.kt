package com.messenger.springMessengerAPI.models

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.util.*


@Entity
@Table(name = "Messages")
class Message(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int = 0,

        @Column(name = "UserId")
        val userId: Int = 0,

        @Column(name = "TextMessage")
        val textMessage: String? = "",

        @Column(name = "TimeSent")
        @Temporal(TemporalType.DATE)
        val timeSent: Date? = null,


        @Column(name = "ConversationId")
        val conversationId: Int = 0,
)