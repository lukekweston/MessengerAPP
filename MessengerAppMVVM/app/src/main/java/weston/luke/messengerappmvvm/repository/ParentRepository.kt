package weston.luke.messengerappmvvm.repository

import android.content.Context
import weston.luke.messengerappmvvm.data.remote.request.LogoutRequest
import weston.luke.messengerappmvvm.util.ImageUtils

class ParentRepository(
    private val conversationRepository: ConversationRepository,
    private val loggedInUserRepository: LoggedInUserRepository,
    private val messageRepository: MessageRepository
) {


    suspend fun logoutUser(context: Context) {
        val loggedInUser = loggedInUserRepository.awaitGettingLoggedInUser()

        //Delete the users fcm_reg_token from server
        loggedInUserRepository.logoutUser(
            LogoutRequest(
                userId = loggedInUser!!.userId,
                userName = loggedInUser.userName
            )
        )

        //Delete users data locally
        loggedInUserRepository.deleteUserFromLocalDatabase()
        conversationRepository.deleteConversationData()
        messageRepository.deleteAllMessages()
        ImageUtils.deleteAllHiddenLowResImages(context)
    }


}