package weston.luke.messengerappmvvm.data.remote.response

data class FriendResponse(
    val friendUserId: Int,
    val friendUserName: String,
    val friendshipStatus: String
)