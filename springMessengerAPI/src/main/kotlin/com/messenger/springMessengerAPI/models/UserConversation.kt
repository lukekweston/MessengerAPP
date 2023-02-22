package com.messenger.springMessengerAPI.models

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.io.Serializable

@Embeddable
data class UserConversationId(
    @Column(name = "UserId")
    val userId: Int = 0,
    @Column(name = "ConversationId")
    val conversationId: Int = 0
) : Serializable {}

@Entity
@Table(name = "UserConversation")
class UserConversation {
    @EmbeddedId
    var id: UserConversationId? = null

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "id", referencedColumnName = "id", nullable = false)
    var user: Users? = null
}