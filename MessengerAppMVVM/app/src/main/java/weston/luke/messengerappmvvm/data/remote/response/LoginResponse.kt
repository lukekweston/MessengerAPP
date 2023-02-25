package weston.luke.messengerappmvvm.data.remote.response

data class LoginResponse(
    val SuccessfulLogin: Boolean,
    val UserEmail: String,
    val UserId: Int,
    val UserName: String
)