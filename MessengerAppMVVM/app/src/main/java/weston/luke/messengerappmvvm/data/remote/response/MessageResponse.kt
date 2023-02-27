package weston.luke.messengerappmvvm.data.remote.response

class MessageResponse : ArrayList<MessageResponseItem>()

data class MessageResponseItem(
    val conversationId: Int,
    val id: Int,
    val message: String,
    val timeSent: String,
    val timeUpdated: String,
    val userId: Int,
    val username: String
)