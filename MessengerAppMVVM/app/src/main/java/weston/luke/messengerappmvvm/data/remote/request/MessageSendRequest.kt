package weston.luke.messengerappmvvm.data.remote.request


data class MessageSendRequest(
    val conversationId: Int,
    val message: String,
    val userId: Int,
    val imageBase64: String? = null
)