package weston.luke.messengerappmvvm.data.remote.response

class ConversationResponse : ArrayList<ConversationResponseItem>()

data class ConversationResponseItem(
    val conversationName: String,
    val id: Int,
    val lastUpdated: String?,
    val success: Boolean
)