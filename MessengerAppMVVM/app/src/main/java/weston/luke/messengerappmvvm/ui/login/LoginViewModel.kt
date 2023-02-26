package weston.luke.messengerappmvvm.ui.login

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser
import weston.luke.messengerappmvvm.data.remote.api.MessengerAPIService
import weston.luke.messengerappmvvm.data.remote.request.LoginRequest
import weston.luke.messengerappmvvm.data.remote.response.ConversationResponse
import weston.luke.messengerappmvvm.data.remote.response.LoginResponse
import weston.luke.messengerappmvvm.repository.ConversationsRepository
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import java.lang.IllegalArgumentException
import java.time.LocalDateTime

class LoginViewModel(
    private val loginRepository: LoggedInUserRepository,
    private val conversationsRepository: ConversationsRepository
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
            conversationsRepository.insertConversations(
                conversationResponse.map {
                    Conversation(
                        conversationId = it.id,
                        conversationName = it.conversationName,
                        lastUpdatedDateTime = if (it.lastUpdated != null) LocalDateTime.parse(it.lastUpdated) else null

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
                            getConversationsForUser(loginResponse.UserId)

//                          Load messages to local database

                            //Login user
                            if (!failedLogin) {
                                addUserToBeLoggedInLocally(loginResponse)
                            } else {
                                _toastMessage.value = "Error logging in, please try again"
                                //Clear converations

                                //Clear messages
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
                        insertConversationsIntoDatabase(conversationResponse)
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
    private val conversationsRepository: ConversationsRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(loginRepository, conversationsRepository) as T

        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}