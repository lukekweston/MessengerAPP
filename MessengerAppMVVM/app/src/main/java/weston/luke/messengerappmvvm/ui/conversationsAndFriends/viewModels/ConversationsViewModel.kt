package weston.luke.messengerappmvvm.ui.conversationsAndFriends.viewModels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import weston.luke.messengerappmvvm.data.database.dto.LatestMessage
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.repository.ConversationRepository
import weston.luke.messengerappmvvm.repository.MessageRepository
import javax.inject.Inject

@HiltViewModel
class ConversationsViewModel
    @Inject constructor(
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