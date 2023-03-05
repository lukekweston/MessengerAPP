package weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser
import weston.luke.messengerappmvvm.repository.ConversationRepository
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import weston.luke.messengerappmvvm.repository.MessageRepository
import weston.luke.messengerappmvvm.util.Utils

class ConversationAndFriendsViewModel(
    private val loggedInUserRepository: LoggedInUserRepository,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    val loggedInUser: LiveData<LoggedInUser?> = loggedInUserRepository.loggedInUser.asLiveData()


    suspend fun logoutUser() {

        Utils.logoutUser(loggedInUserRepository, conversationRepository, messageRepository, loggedInUser.value!!.userId, loggedInUser.value!!.userName)

    }
}


class ConversationAndFriendsViewModelFactory(
    private val loggedInUserRepository: LoggedInUserRepository,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversationAndFriendsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConversationAndFriendsViewModel(
                loggedInUserRepository,
                conversationRepository,
                messageRepository
            ) as T

        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}