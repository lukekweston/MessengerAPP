package weston.luke.messengerappmvvm.ui.login

import androidx.lifecycle.*
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser
import weston.luke.messengerappmvvm.data.remote.request.LoginRequest
import weston.luke.messengerappmvvm.repository.ConversationRepository
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import weston.luke.messengerappmvvm.repository.MessageRepository

class LoginViewModel(
    private val loginRepository: LoggedInUserRepository,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private var failedLogin = false

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



    fun loginUser(userName: String, password: String) {

        failedLogin = false


        viewModelScope.launch {
            try {
                var loginResponse = loginRepository.loginUser(
                    LoginRequest(
                        userName = userName,
                        password = password
                    )
                )
                if (loginResponse.SuccessfulLogin) {
                    //Get the conversation data
                    conversationRepository.getAllConversationsForUser(loginResponse.UserId)
                    //Get all the messages for the user
                    messageRepository.getAllMessagesForUser(loginResponse.UserId)
                    //Set the logged in user to this in the database
                    loginRepository.loginUser(
                        LoggedInUser(
                            userId = loginResponse.UserId,
                            userName = loginResponse.UserName,
                            userEmail = loginResponse.UserEmail
                        )
                    )
                }
                //Invalid username or password
                else {
                    _invalidUserNameOrPassword.value = true
                }
            } catch (e: Exception) {

            }
        }
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