package weston.luke.messengerappmvvm.util

object Constants {

    const val BASE_URL: String = "https://202.74.216.209:17300/"

    const val API_ENDPOINT_LOGIN_USER = "loginUser"
    const val API_ENDPOINT_LOGOUT_USER = "logoutUser"
    const val API_ENDPOINT_CHECK_FCM_REG_TOKEN = "checkFCMRegToken"
    const val API_ENDPOINT_GET_CONVERSATIONS_FOR_USER = "conversationsForUser/{userId}"
    const val API_ENDPOINT_GET_ALL_MESSAGES_FOR_USER = "allMessagesForUser/{userId}"
    const val API_ENDPOINT_SEND_MESSAGE = "sendMessage"
    const val API_ENDPOINT_GET_LOW_RES_IMAGE_FOR_MESSAGE = "message/{messageId}/lowResImage"
    const val API_ENDPOINT_GET_FULL_RES_IMAGE_FOR_MESSAGE = "/message/{messageId}/fullResImage"

    const val API_ENDPOINT_GET_ALL_FRIENDS = "findAllFriendsForUser/{userId}"
    const val API_ENDPOINT_SEND_FRIEND_REQUEST = "sendFriendRequest"
    const val API_ENDPOINT_UPDATE_FRIENDSHIP_STATUS = "updateFriendStatus"


    const val CONVERSATION_ID = "conversationId"
    const val OPEN_CONVERSATION_FROM_NOTIFICATION = "openConversationFromNotification"

    const val IMAGE_ID = "imageId"
    const val IMAGE_BY_USERNAME = "imageByName"
    const val IMAGE_FILE_NAME = "imageFileName"

    const val GOTO_FRIEND_FRAGMENT = "friendFragment"
}