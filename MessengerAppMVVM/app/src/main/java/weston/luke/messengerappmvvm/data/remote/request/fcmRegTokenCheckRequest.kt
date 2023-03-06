package weston.luke.messengerappmvvm.data.remote.request

data class fcmRegTokenCheckRequest(
    val firebaseRegistrationToken: String,
    val userId: Int
)