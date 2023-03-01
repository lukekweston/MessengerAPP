package weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.data.database.entities.Message
import weston.luke.messengerappmvvm.repository.ConversationRepository
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import weston.luke.messengerappmvvm.repository.MessageRepository
import weston.luke.messengerappmvvm.ui.conversationsAndFriends.data.ConversationWithLatestMessage
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ConversationsViewModel(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    // private var conversations :List<Conversation> = listOf()


    private val _conversationsWithLatestMessages =
        MutableLiveData<List<ConversationWithLatestMessage>>()

    val conversationsWithLatestMessages: LiveData<List<ConversationWithLatestMessage>>
        get() = _conversationsWithLatestMessages


    private val _conversations = MutableLiveData<List<Conversation>>()
    val conversations: LiveData<List<Conversation>> = _conversations


    fun loadConversations() {

        viewModelScope.launch {
            conversationRepository.conversations.collect { conversations ->
                _conversations.value = conversations

                val conversationsWithLatestMessageTemp: MutableList<ConversationWithLatestMessage> =
                    mutableListOf()

                for (conversation in conversations) {

                    val latestMessage = messageRepository.getLatestMessageForConversation(conversation.conversationId)


                    conversationsWithLatestMessageTemp += ConversationWithLatestMessage(
                        conversationId = conversation.conversationId,
                        conversationName = conversation.conversationName ?: "Unnamed Conversation",
                        userName = latestMessage.userName ?: "",
                        message = latestMessage.message ?: "",
                        lastMessageTime = latestMessage.latestTime
                    )
                }

                _conversationsWithLatestMessages.value = conversationsWithLatestMessageTemp
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