package weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.repository.ConversationsRepository
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import java.lang.IllegalArgumentException

class ConversationsViewModel(conversationsRepository: ConversationsRepository) : ViewModel(){

    val conversations: LiveData<List<Conversation>> = conversationsRepository.conversations.asLiveData()
}


class ConversationsViewModelFactory( private val conversationsRepository: ConversationsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversationsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConversationsViewModel(conversationsRepository) as T

        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}