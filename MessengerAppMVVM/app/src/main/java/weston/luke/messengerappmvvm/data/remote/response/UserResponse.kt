package weston.luke.messengerappmvvm.data.remote.response

data class UserResponse(
    val id: Int,
    val password: String,
    val userConversation: List<UserConversation>,
    val useremail: String,
    val username: String
)

data class UserConversation(
    val id: Id
)

data class Id(
    val conversationId: Int,
    val userId: Int
)
