package weston.luke.messengerappmvvm.ui.login

import androidx.lifecycle.*
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

    val loggedInUser: LiveData<LoggedInUser?> = loginRepository.loggedInUser.asLiveData()

    var firebaseToken: String = ""

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
        firebaseToken = loginRepository.getFirebaseToken()
        return loginRepository.awaitGettingLoggedInUser() != null
    }



    fun loginUser(userName: String, password: String) {

        viewModelScope.launch {
            try {
                val loginResponse = loginRepository.loginUser(
                    LoginRequest(
                        userName = userName,
                        password = password,
                        firebaseRegistrationToken = firebaseToken
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
                e
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