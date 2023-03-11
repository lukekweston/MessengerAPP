package weston.luke.messengerappmvvm.util

object Constants {

    const val BASE_URL: String = "http://192.168.1.3:8000/"

    const val API_ENDPOINT_LOGIN_USER = "loginUser"
    const val API_ENDPOINT_LOGOUT_USER = "logoutUser"
    const val API_ENDPOINT_CHECK_FCM_REG_TOKEN = "checkFCMRegToken"
    const val API_ENDPOINT_GET_CONVERSATIONS_FOR_USER = "getConversationsForUser/"
    const val API_ENDPOINT_GET_ALL_MESSAGES_FOR_USER = "allMessagesForUser/"
    const val API_ENDPOINT_SEND_MESSAGE = "sendMessage"
    const val API_ENDPOINT_GET_LOW_RES_IMAGE_FOR_MESSAGE = "getLowResImageForMessage/"
    const val API_ENDPOINT_GET_FULL_RES_IMAGE_FOR_MESSAGE = "getFullResImageForMessage/"

    const val API_ENDPOINT_GET_ALL_FRIENDS = "findAllFriendsForUser/"
    const val API_ENDPOINT_SEND_FRIEND_REQUEST = "sendFriendRequest"
    const val API_ENDPOINT_UPDATE_FRIENDSHIP_STATUS = "updateFriendStatus"


    const val CONVERSATION_ID = "conversationId"

    const val IMAGE_ID = "imageId"
    const val IMAGE_BY_USERNAME = "imageByName"
    const val IMAGE_FILE_NAME = "imageFileName"

    const val GOTO_FRIEND_FRAGMENT = "friendFragment"
}