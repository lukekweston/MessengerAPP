package weston.luke.messengerappmvvm.data.remote.api

import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import weston.luke.messengerappmvvm.data.remote.request.LoginRequest
import weston.luke.messengerappmvvm.data.remote.response.ConversationResponse
import weston.luke.messengerappmvvm.data.remote.response.LoginResponse
import weston.luke.messengerappmvvm.data.remote.response.UserResponse
import weston.luke.messengerappmvvm.util.Constants

interface messengerAPIInterface {

    @GET(Constants.API_ENDPOINT_ALL_USERS)
    fun getAllUsers(): Single<List<UserResponse>>


    @POST(Constants.API_ENDPOINT_LOGIN_USER)
    fun loginUser(
        @Body loginRequest: LoginRequest
    ): Single<LoginResponse>

    @GET(Constants.API_ENDPOINT_GET_CONVERSATIONS_FOR_USER + "{userId}")
    fun getAllConversationsForUser(
        @Path("userId") userId: Int
    ): Single<ConversationResponse>
}