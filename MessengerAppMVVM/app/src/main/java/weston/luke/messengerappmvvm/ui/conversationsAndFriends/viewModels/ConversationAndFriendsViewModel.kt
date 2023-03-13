package weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import weston.luke.messengerappmvvm.repository.ParentRepository

class ConversationAndFriendsViewModel(
    private val loggedInUserRepository: LoggedInUserRepository,
    private val parentRepository: ParentRepository
) : ViewModel() {

    val loggedInUser: LiveData<LoggedInUser?> = loggedInUserRepository.loggedInUser


    suspend fun logoutUser(context: Context) {
        parentRepository.logoutUser(context)
    }
}


class ConversationAndFriendsViewModelFactory(
    private val loggedInUserRepository: LoggedInUserRepository,
    private val parentRepository: ParentRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversationAndFriendsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConversationAndFriendsViewModel(
                loggedInUserRepository,
                parentRepository
            ) as T

        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}