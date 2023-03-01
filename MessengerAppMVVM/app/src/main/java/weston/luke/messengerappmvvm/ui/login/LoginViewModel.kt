package weston.luke.messengerappmvvm.ui.login

import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import androidx.room.ColumnInfo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser
import weston.luke.messengerappmvvm.data.database.entities.Message
import weston.luke.messengerappmvvm.data.database.entities.SentStatus
import weston.luke.messengerappmvvm.data.remote.api.MessengerAPIService
import weston.luke.messengerappmvvm.data.remote.request.LoginRequest
import weston.luke.messengerappmvvm.data.remote.response.ConversationResponse
import weston.luke.messengerappmvvm.data.remote.response.LoginResponse
import weston.luke.messengerappmvvm.data.remote.response.MessageResponse
import weston.luke.messengerappmvvm.repository.ConversationRepository
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import weston.luke.messengerappmvvm.repository.MessageRepository
import java.lang.IllegalArgumentException
import java.time.LocalDateTime

class LoginViewModel(
    private val loginRepository: LoggedInUserRepository,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private var failedLogin = false

    private val api = MessengerAPIService()

    val loggedInUser: LiveData<LoggedInUser?> = loginRepository.loggedInUser.asLiveData()

    private val _toastMessage = MutableLiveData<String>()

    val toastMessage: LiveData<String>
        get() = _toastMessage

    private val _invalidUserNameOrPassword = MutableLiveData<Boolean>()

    val invalidUserNameOrPassword: LiveData<Boolean>
        get() = _invalidUserNameOrPassword


    private val _loggingUserIn = MutableLiveData<Boolean>()

    val loggingUserIn: LiveData<Boolean>
        get() = _loggingUserIn


    suspend fun checkUserAlreadyLoggedIn(): Boolean {
        return loginRepository.awaitGettingLoggedInUser() != null
    }

    @WorkerThread
    fun addUserToBeLoggedInLocally(loginResponse: LoginResponse) = viewModelScope.launch {
        loginRepository.loginUser(
            LoggedInUser(
                userId = loginResponse.UserId,
                userName = loginResponse.UserName,
                userEmail = loginResponse.UserEmail
            )
        )
    }

    @WorkerThread
    fun insertConversationsIntoDatabase(conversationResponse: ConversationResponse) =
        viewModelScope.launch {
            conversationRepository.insertConversations(
                conversationResponse.map {
                    Conversation(
                        conversationId = it.id,
                        conversationName = it.conversationName,
                        lastUpdatedDateTime = if (it.lastUpdated != null) LocalDateTime.parse(it.lastUpdated) else null

                    )
                }
            )
        }

    @WorkerThread
    fun insertMessagesIntoDatabase(messageResponse: MessageResponse) =
        viewModelScope.launch {
            messageRepository.insertMessages(
                messageResponse.map {
                    Message(
                        messageId = it.id,
                        userId = it.userId,
                        conversationId = it.conversationId,
                        userName = it.username,
                        message = it.message,
                        timeSent = LocalDateTime.parse(it.timeSent),
                        timeUpdated = if (it.timeUpdated != null) LocalDateTime.parse(it.timeUpdated) else null,
                        status = SentStatus.RECEIVED_FROM_API
                    )
                }
            )
        }


    fun loginUser(userName: String, password: String) {

        failedLogin = false

        compositeDisposable.add(
            api.loginUser(LoginRequest(userName = userName, password = password))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<LoginResponse>() {
                    override fun onSuccess(loginResponse: LoginResponse) {
                        if (loginResponse.SuccessfulLogin) {
                            _loggingUserIn.value = true
                            _invalidUserNameOrPassword.value = false


                            //If either of these methods fail - do not log in user
//                          Load conversations to local database
                            CoroutineScope(Dispatchers.Main).launch {
                                async { getConversationsForUser(loginResponse.UserId) }.await()
//                          Load messages to local database
                                async { getMessagesForUser(loginResponse.UserId) }.await()


                                //Login user
                                if (!failedLogin) {
                                    async {addUserToBeLoggedInLocally(loginResponse)}.await()
                                } else {
                                    _toastMessage.value = "Error logging in, please try again"
                                    //Clear conversations

                                    //Clear messages
                                }
                            }
//                            Start worker to poll and get messages/conversations
                        } else {
                            _invalidUserNameOrPassword.value = true
                        }

                    }

                    override fun onError(e: Throwable) {
                        _toastMessage.value = "Error logging in, please try again"
                    }
                })
        )
    }


     fun getConversationsForUser(userId: Int) {

        compositeDisposable.add(
            api.getAllConversationsForUser(userId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ConversationResponse>() {
                    override fun onSuccess(conversationResponse: ConversationResponse) {
                        //Insert conversations into local database
                        CoroutineScope(Dispatchers.Main).launch {
                            async { insertConversationsIntoDatabase(conversationResponse) }.await()
                        }

                    }

                    override fun onError(e: Throwable) {
                        //Set failed to true
                        failedLogin = true
                    }
                })
        )
    }

    fun getMessagesForUser(userId: Int) {

        compositeDisposable.add(
            api.getAllMessagesForUser(userId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MessageResponse>() {
                    override fun onSuccess(messageResponse: MessageResponse) {
                        //Insert messages into local database
                        CoroutineScope(Dispatchers.Main).launch {
                            async { insertMessagesIntoDatabase(messageResponse) }.await()
                        }
                    }
                    override fun onError(e: Throwable) {
                        //Set failed to true
                        failedLogin = true
                    }
                })
        )
    }

}

class LoginViewModelFactory(
    private val loginRepository: LoggedInUserRepository,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(loginRepository, conversationRepository, messageRepository) as T

        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}