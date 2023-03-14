package weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import weston.luke.messengerappmvvm.repository.ParentRepository
import javax.inject.Inject

@HiltViewModel
class ConversationAndFriendsViewModel
    @Inject constructor(
    loggedInUserRepository: LoggedInUserRepository,
    private val parentRepository: ParentRepository
) : ViewModel() {

    val loggedInUser: LiveData<LoggedInUser?> = loggedInUserRepository.loggedInUser


    suspend fun logoutUser(context: Context) {
        parentRepository.logoutUser(context)
    }
}