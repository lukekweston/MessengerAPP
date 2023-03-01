package weston.luke.messengerappmvvm.ui.messages

import androidx.lifecycle.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.data.database.entities.Message
import weston.luke.messengerappmvvm.data.remote.api.MessengerAPIService
import weston.luke.messengerappmvvm.data.remote.request.LoginRequest
import weston.luke.messengerappmvvm.data.remote.request.MessageSendRequest
import weston.luke.messengerappmvvm.data.remote.response.LoginResponse
import weston.luke.messengerappmvvm.data.remote.response.SuccessResponse
import weston.luke.messengerappmvvm.repository.ConversationRepository
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import weston.luke.messengerappmvvm.repository.MessageRepository
import weston.luke.messengerappmvvm.ui.login.LoginViewModel
import java.lang.IllegalArgumentException

class MessagesViewModel(
    private val loginRepository: LoggedInUserRepository,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val api = MessengerAPIService()

    val messages = MutableLiveData<List<Message>>()
    val loggedInUserId = MutableLiveData<Int>()

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
                loggedInUserId.value = it?.userId
            }
        }
        viewModelScope.launch {
            messageRepository.getAllMessagesForAConversation(conversationId).collect {
                messages.value = it
            }
        }
    }

    fun sendMessage(message: String, conversationId: Int) {

        viewModelScope.launch {
            api.sendMessage(
                MessageSendRequest(
                    userId = loggedInUserId.value!!,
                    message = message,
                    conversationId = conversationId
                )
            )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<SuccessResponse>() {
                    override fun onSuccess(response: SuccessResponse) {

                    }

                    override fun onError(e: Throwable) {

                    }
                })
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

