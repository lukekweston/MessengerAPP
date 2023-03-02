package weston.luke.messengerappmvvm.ui.messages

import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser
import weston.luke.messengerappmvvm.data.database.entities.Message
import weston.luke.messengerappmvvm.data.database.entities.SentStatus
import weston.luke.messengerappmvvm.repository.ConversationRepository
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import weston.luke.messengerappmvvm.repository.MessageRepository
import java.time.LocalDateTime

class MessagesViewModel(
    private val loginRepository: LoggedInUserRepository,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {



    private val _toastMessageToDisplay =
        MutableLiveData<String>()

    val toastMessageToDisplay: LiveData<String>
        get() = _toastMessageToDisplay

    val loggedInUser = MutableLiveData<LoggedInUser>()

    val messages = MutableLiveData<List<Message>>()
    private val loggedInUserId = MutableLiveData<Int>()

    val loggedInUserAndMessages = MediatorLiveData<Pair<List<Message>, Int>>()

    init {
        loggedInUserAndMessages.addSource(messages) {
            loggedInUserAndMessages.value = Pair(it, loggedInUserId.value ?: 0)
        }
        loggedInUserAndMessages.addSource(loggedInUserId) {
            loggedInUserAndMessages.value = Pair(messages.value.orEmpty(), it)
        }
    }


    fun getConversation(conversationId: Int): Flow<Conversation> {
        return conversationRepository.getConversation(conversationId)
    }

    fun loadData(conversationId: Int) {
        viewModelScope.launch {
            loginRepository.loggedInUser.collect {
                loggedInUser.value = it
                loggedInUserId.value = it?.userId
            }
        }
        viewModelScope.launch {
            messageRepository.getAllMessagesForAConversation(conversationId).collect {
                messages.value = it
            }
        }
    }

    fun sendMessage(textMessage: String, conversationId: Int) {
        viewModelScope.launch {
            var message = Message(
                conversationId = conversationId,
                message = textMessage,
                userId = loggedInUser.value!!.userId,
                userName = loggedInUser.value!!.userName,
                status = SentStatus.CREATED,
                timeSent = LocalDateTime.now()
            )
            var success = messageRepository.sendMessage(message)

            if(!success){
                _toastMessageToDisplay.value = "Error contacting the server"
            }
        }
    }
}

class MessagesViewModelFactory(
    private val loginRepository: LoggedInUserRepository,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessagesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MessagesViewModel(
                loginRepository,
                conversationRepository,
                messageRepository
            ) as T

        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}

