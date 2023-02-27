package weston.luke.messengerappmvvm.ui.conversationsAndFriends.data


import java.time.LocalDateTime

data class ConversationWithLatestMessage(
    val conversationId: Int,
    val conversationName: String,
    val userName: String,
    val message: String,
    val lastMessageTime: LocalDateTime
)
