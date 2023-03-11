package weston.luke.messengerappmvvm.data.remote.response

data class FriendRequestResponse(
    val alreadyFriends: Boolean,
    val friendRequestAlreadySent: Boolean,
    val friendRequestSent: Boolean,
    val usernameOrEmailNotFound: Boolean,
    val friendUserId: Int?,
    val friendUserName: String?,
)