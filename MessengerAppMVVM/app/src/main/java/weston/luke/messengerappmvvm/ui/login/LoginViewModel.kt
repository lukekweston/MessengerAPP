package weston.luke.messengerappmvvm.ui.login

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser
import weston.luke.messengerappmvvm.data.remote.request.LoginRequest
import weston.luke.messengerappmvvm.data.remote.request.fcmRegTokenCheckRequest
import weston.luke.messengerappmvvm.repository.ConversationRepository
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import weston.luke.messengerappmvvm.repository.MessageRepository
import weston.luke.messengerappmvvm.repository.ParentRepository

class LoginViewModel(
    private val loginRepository: LoggedInUserRepository,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val parentRepository: ParentRepository
) : ViewModel() {

    //Bool to keep track for if the user has/is successfully logged in and should go to the next screen
    private val _successfullyCheckedUserIsLoggedIn = MutableLiveData<Boolean>()

    val successfullyCheckedUserIsLoggedIn: LiveData<Boolean>
        get() = _successfullyCheckedUserIsLoggedIn


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


    fun checkUserAlreadyLoggedIn(context: Context) {
        viewModelScope.launch {

            firebaseToken = loginRepository.getFirebaseToken()

            var loggedInUser = loginRepository.awaitGettingLoggedInUser()

            //There is a logged in user
            if (loggedInUser != null) {

                //Check that the users logged in firebase reg token matches the saved firebase token for the logged in token
                val success = loginRepository.checkFcmRegToken(
                    fcmRegTokenCheckRequest(
                        userId = loggedInUser.userId,
                        firebaseRegistrationToken = firebaseToken
                    )
                )

                //If they dont, log the user out. Display a message informing the user why they have been logged out
                if (!success.success) {
                    _toastMessage.value =
                        "The user has logged in from another device, and as a result, they have been logged out of this device."
                    parentRepository.logoutUser(context)
                }
                //Else user is already logged in, continue to next screen
                else {
                    _successfullyCheckedUserIsLoggedIn.value = true
                }
            }
        }

    }



    fun loginUser(userName: String, password: String, context: Context) {

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
                    messageRepository.getAllMessagesForUser(loginResponse.UserId, context)
                    //Set the logged in user to this in the database
                    loginRepository.loginUser(
                        LoggedInUser(
                            userId = loginResponse.UserId,
                            userName = loginResponse.UserName,
                            userEmail = loginResponse.UserEmail
                        )
                    )
                    _successfullyCheckedUserIsLoggedIn.value = true
                }
                //Invalid username or password
                else {
                    _invalidUserNameOrPassword.value = true
                }
            } catch (e: Exception) {
                _toastMessage.value = "Unable to contact server, please try again later"
            }
        }
    }

}

class LoginViewModelFactory(
    private val loginRepository: LoggedInUserRepository,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val parentRepository: ParentRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(loginRepository, conversationRepository, messageRepository, parentRepository) as T

        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}