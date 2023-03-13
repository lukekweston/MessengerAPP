package weston.luke.messengerappmvvm.data.remote.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import weston.luke.messengerappmvvm.data.remote.request.*
import weston.luke.messengerappmvvm.data.remote.response.*
import weston.luke.messengerappmvvm.util.Constants

interface MessengerAPIInterface {


    //Checks a users username and password to see for valid login
    @POST(Constants.API_ENDPOINT_LOGIN_USER)
    suspend fun loginUser(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    //Logs out user by deleting fcm key saved in database against the user
    @POST(Constants.API_ENDPOINT_LOGOUT_USER)
    suspend fun logoutUser(
        @Body logoutRequest: LogoutRequest
    ): SuccessResponse

    @POST(Constants.API_ENDPOINT_CHECK_FCM_REG_TOKEN)
    suspend fun checkFcmRegToken(
        @Body fcmRegTokenCheckRequest: fcmRegTokenCheckRequest
    ): SuccessResponse

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

    @GET(Constants.API_ENDPOINT_GET_LOW_RES_IMAGE_FOR_MESSAGE + "{messageId}")
    suspend fun getLowResImageForMessage(
        @Path("messageId") messageId: Int
    ): ImageResponse

    @GET(Constants.API_ENDPOINT_GET_FULL_RES_IMAGE_FOR_MESSAGE + "{messageId}")
    suspend fun getFullResImageForMessage(
        @Path("messageId") messageId: Int
    ): ImageResponse

    @POST(Constants.API_ENDPOINT_SEND_FRIEND_REQUEST)
    suspend fun sendFriendRequest(
        @Body friendRequest: NewFriendRequest
    ): FriendRequestResponse


    @GET(Constants.API_ENDPOINT_GET_ALL_FRIENDS + "{userId}")
    suspend fun getAllFriendsForUser(
        @Path("userId") userId: Int
    ): List<FriendResponse>

    @POST(Constants.API_ENDPOINT_UPDATE_FRIENDSHIP_STATUS)
    suspend fun updateFriendshipStatus(
        @Body updateFriendshipStatusRequest: UpdateFriendshipStatusRequest
    ): ConversationResponseItem



}