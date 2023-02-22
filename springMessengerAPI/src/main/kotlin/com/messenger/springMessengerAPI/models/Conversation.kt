package com.messenger.springMessengerAPI.models

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*


@Entity
@Table(name = "Conversations")
class Conversation(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int = 0,

        @Column(name = "ConversationName")
        val conversationName: String? = null,


        @JsonManagedReference
        @OneToMany(mappedBy = "conversation")
        val userConversation: MutableList<UserConversation> = mutableListOf()
)