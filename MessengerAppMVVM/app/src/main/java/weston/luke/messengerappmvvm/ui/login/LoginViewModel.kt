package weston.luke.messengerappmvvm.ui.login

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser
import weston.luke.messengerappmvvm.data.remote.request.LoginRequest
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import weston.luke.messengerappmvvm.repository.ParentRepository

class LoginViewModel(
    private val loginRepository: LoggedInUserRepository,
    private val parentRepository: ParentRepository,
) : ViewModel() {


    val loggedInUser = loginRepository.loggedInUser


    private val _toastMessage = MutableLiveData<String>()

    val toastMessage: LiveData<String>
        get() = _toastMessage

    private val _invalidUserNameOrPassword = MutableLiveData<Boolean>()

    val invalidUserNameOrPassword: LiveData<Boolean>
        get() = _invalidUserNameOrPassword


    private val _loggingUserIn = MutableLiveData<Boolean>()

    val loggingUserIn: LiveData<Boolean>
        get() = _loggingUserIn


    fun loginUser(userName: String, password: String, context: Context) {

        viewModelScope.launch {
            try {
                val firebaseToken = loginRepository.getFirebaseToken()

                val loginResponse = loginRepository.loginUser(
                    LoginRequest(
                        userName = userName,
                        password = password,
                        firebaseRegistrationToken = firebaseToken
                    )
                )
                if (loginResponse.SuccessfulLogin) {
                    parentRepository.getAllDataForUser(loginResponse.UserId, context)
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
                _toastMessage.value = "Unable to contact server, please try again later"
            }
        }
    }

}

class LoginViewModelFactory(
    private val loginRepository: LoggedInUserRepository,
    private val parentRepository: ParentRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(loginRepository, parentRepository) as T

        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}