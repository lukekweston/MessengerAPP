package weston.luke.messengerappmvvm.data.remote.request


data class UpdateFriendshipStatusRequest(
    val selfUserId: Int,
    val friendUsername: String,
    val friendshipStatus: String
)