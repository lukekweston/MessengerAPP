package weston.luke.messengerappmvvm.repository

import android.content.Context
import weston.luke.messengerappmvvm.data.remote.request.LogoutRequest
import weston.luke.messengerappmvvm.util.ImageUtils

class ParentRepository(
    private val conversationRepository: ConversationRepository,
    private val loggedInUserRepository: LoggedInUserRepository,
    private val messageRepository: MessageRepository,
    private val friendRepository: FriendRepository
) {

    val loggedInUser = loggedInUserRepository.loggedInUser


    suspend fun logoutUser(context: Context) {

        //Delete the users fcm_reg_token from server
        loggedInUserRepository.logoutUser(
            LogoutRequest(
                userId = loggedInUser.value!!.userId,
                username = loggedInUser.value!!.userName
            )
        )

        //Delete users data locally
        loggedInUserRepository.deleteUserFromLocalDatabase()
        conversationRepository.deleteConversationData()
        messageRepository.deleteAllMessages()
        friendRepository.deleteAllFriends()
        ImageUtils.deleteAllHiddenLowResImages(context)
    }

    suspend fun getAllDataForUser(userId: Int, context: Context){
        //Get the conversation data
        conversationRepository.getAllConversationsForUser(userId)
        //Get all the messages for the user
        messageRepository.getAllMessagesForUser(userId, context)
        //Get all the friends for a logged in user
        friendRepository.getAllFriendsForUser(userId)
    }


}