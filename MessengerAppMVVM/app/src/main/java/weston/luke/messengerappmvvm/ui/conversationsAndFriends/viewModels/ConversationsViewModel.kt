package weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import weston.luke.messengerappmvvm.data.database.dto.LatestMessage
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.repository.ConversationRepository
import weston.luke.messengerappmvvm.repository.MessageRepository

class ConversationsViewModel(
    conversationRepository: ConversationRepository,
    messageRepository: MessageRepository
) : ViewModel() {

    private val latestMessages = messageRepository.getLatestMessagesForEachConversation()
    private val conversations = conversationRepository.getConversations()

    val latestMessagesAndConversations = MediatorLiveData<Pair<List<LatestMessage?>, List<Conversation>>>()

    init {
        latestMessagesAndConversations.addSource(latestMessages) {
            latestMessagesAndConversations.value = Pair(it, conversations.value.orEmpty())
        }
        latestMessagesAndConversations.addSource(conversations) {
            latestMessagesAndConversations.value = Pair(latestMessages.value.orEmpty(), it)
        }
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