package weston.luke.messengerappmvvm.data.remote.request


data class LoginRequest(
    val password: String,
    val userName: String,
    val firebaseRegistrationToken: String
)