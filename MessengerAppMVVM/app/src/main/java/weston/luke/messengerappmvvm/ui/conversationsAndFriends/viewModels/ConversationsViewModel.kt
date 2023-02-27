package weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.repository.ConversationRepository
import weston.luke.messengerappmvvm.repository.MessageRepository
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.data.ConversationWithLatestMessage
import java.lang.IllegalArgumentException

class ConversationsViewModel(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    val conversations: LiveData<List<Conversation>> =
        conversationRepository.conversations.asLiveData()



    private fun conversationsWithMessages(): LiveData<List<ConversationWithLatestMessage>>{

        val conversations = conversationRepository.conversations

        //Get the latest message for a conversation

        //Order conversations by these messages

        return conversationRepository.conversations.asLiveData()
    }
}


class ConversationsViewModelFactory(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversationsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConversationsViewModel(conversationRepository, messageRepository) as T

        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}