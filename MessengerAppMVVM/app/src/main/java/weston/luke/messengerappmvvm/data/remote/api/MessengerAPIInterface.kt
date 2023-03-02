package weston.luke.messengerappmvvm.data.remote.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import weston.luke.messengerappmvvm.data.remote.request.LoginRequest
import weston.luke.messengerappmvvm.data.remote.request.MessageSendRequest
import weston.luke.messengerappmvvm.data.remote.response.ConversationResponse
import weston.luke.messengerappmvvm.data.remote.response.LoginResponse
import weston.luke.messengerappmvvm.data.remote.response.MessageResponse
import weston.luke.messengerappmvvm.data.remote.response.MessageResponseList
import weston.luke.messengerappmvvm.util.Constants

interface MessengerAPIInterface {


    @POST(Constants.API_ENDPOINT_LOGIN_USER)
    suspend fun loginUser(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    @GET(Constants.API_ENDPOINT_GET_CONVERSATIONS_FOR_USER + "{userId}")
    suspend fun getAllConversationsForUser(
        @Path("userId") userId: Int
    ): ConversationResponse

    @GET(Constants.API_ENDPOINT_GET_ALL_MESSAGES_FOR_USER + "{userId}")
    suspend fun getAllMessagesForUser(
        @Path("userId") userId: Int
    ): MessageResponseList

    @POST(Constants.API_ENDPOINT_SEND_MESSAGE)
    suspend fun sendMessage(
        @Body messageRequest: MessageSendRequest
    ): MessageResponse

}