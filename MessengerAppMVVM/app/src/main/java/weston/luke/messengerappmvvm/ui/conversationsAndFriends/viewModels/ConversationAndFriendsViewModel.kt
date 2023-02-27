package weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser
import weston.luke.messengerappmvvm.repository.ConversationsRepository
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import java.lang.IllegalArgumentException

class ConversationAndFriendsViewModel(private val loggedInUserRepository: LoggedInUserRepository, private val conversationsRepository: ConversationsRepository) : ViewModel(){

    val loggedInUser: LiveData<LoggedInUser?> = loggedInUserRepository.loggedInUser.asLiveData()

    @WorkerThread
    suspend fun logoutUser(){
        loggedInUserRepository.logoutUser()
        conversationsRepository.deleteConversationData()
    }
}


class ConversationAndFriendsViewModelFactory(private val loggedInUserRepository: LoggedInUserRepository,  private val conversationsRepository: ConversationsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversationAndFriendsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConversationAndFriendsViewModel(loggedInUserRepository, conversationsRepository) as T

        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}