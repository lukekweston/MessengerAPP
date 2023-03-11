package weston.luke.messengerappmvvm.data.remote.request


data class NewFriendRequest(
    val selfUserId: Int,
    val usernameOrEmail: String
)