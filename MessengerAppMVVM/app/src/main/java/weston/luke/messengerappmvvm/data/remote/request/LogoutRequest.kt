package weston.luke.messengerappmvvm.data.remote.request


data class LogoutRequest(
    val userId: Int,
    val username: String
)