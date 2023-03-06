package weston.luke.messengerappmvvm.ui.shared

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.data.remote.request.LogoutRequest
import weston.luke.messengerappmvvm.data.remote.request.fcmRegTokenCheckRequest
import weston.luke.messengerappmvvm.repository.ConversationRepository
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import weston.luke.messengerappmvvm.repository.MessageRepository


//Todo make a shared view model for checking that the user is not logged in on other devices
class SharedViewModel(
    private val loginRepository: LoggedInUserRepository,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    //Bool to keep track for if the user has/is successfully logged in and should go to the next screen
    private val _successfullyCheckedUserIsLoggedIn = MutableLiveData<Boolean>()

    val successfullyCheckedUserIsLoggedIn: LiveData<Boolean>
        get() = _successfullyCheckedUserIsLoggedIn
    

    var firebaseToken: String = ""

    private val _toastMessage = MutableLiveData<String>()


    fun checkUserAlreadyLoggedIn() {
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
                    logoutUser()
                }
                //Else user is already logged in, continue to next screen
                else {
                    _successfullyCheckedUserIsLoggedIn.value = true
                }
            }
        }

    }

    suspend fun logoutUser() {
        val loggedInUser = loginRepository.awaitGettingLoggedInUser()
        //Delete the users fcm_reg_token from server
        loginRepository.logoutUser(
            LogoutRequest(
                userId = loggedInUser!!.userId,
                userName = loggedInUser!!.userName
            )
        )

        //Delete users data locally
        loginRepository.deleteUserFromLocalDatabase()
        conversationRepository.deleteConversationData()
        messageRepository.deleteAllMessages()


    }


}

class SharedViewModelFactory(
    private val loginRepository: LoggedInUserRepository,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedViewModel(loginRepository, conversationRepository, messageRepository) as T

        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}