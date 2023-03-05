package weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.data.database.dto.LatestMessage
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.repository.ConversationRepository
import weston.luke.messengerappmvvm.repository.MessageRepository
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.data.ConversationWithLatestMessage

class ConversationsViewModel(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {



    private val _conversationsWithLatestMessages =
        MutableLiveData<List<ConversationWithLatestMessage>>()

    val conversationsWithLatestMessages: LiveData<List<ConversationWithLatestMessage>>
        get() = _conversationsWithLatestMessages




    private val latestMessages = MutableLiveData<List<LatestMessage?>>()
    private val conversations = MutableLiveData<List<Conversation>>()

    val latestMessagesAndConversations = MediatorLiveData<Pair<List<LatestMessage?>, List<Conversation>>>()

    init {
        latestMessagesAndConversations.addSource(latestMessages) {
            latestMessagesAndConversations.value = Pair(it, conversations.value.orEmpty())
        }
        latestMessagesAndConversations.addSource(conversations) {
            latestMessagesAndConversations.value = Pair(latestMessages.value.orEmpty(), it)
        }
    }



    fun loadConversations() {

        viewModelScope.launch {
            messageRepository.getLastestMessagesForEachConversation().collect{
                latestMessages.value = it
            }
        }

        viewModelScope.launch {
            conversationRepository.conversations.collect { convo ->
                conversations.value = convo
            }
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