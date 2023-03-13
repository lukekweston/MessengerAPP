package weston.luke.messengerappmvvm.ui.splashScreen

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.data.remote.request.fcmRegTokenCheckRequest
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import weston.luke.messengerappmvvm.repository.ParentRepository
import weston.luke.messengerappmvvm.util.toast

class SplashActivityViewModel(
    private val loginRepository: LoggedInUserRepository,
    private val parentRepository: ParentRepository,
) : ViewModel() {

    //Bool to keep track for if the user has/is successfully logged in and should go to the next screen
    private val _userAlreadyLoggedIn = MutableLiveData<Boolean>()

    val userAlreadyLoggedIn: LiveData<Boolean>
        get() = _userAlreadyLoggedIn

    private val loggedInUser = loginRepository.loggedInUser


    fun checkUserAlreadyLoggedIn(context: Context) {
        viewModelScope.launch {

            var firebaseToken = loginRepository.getFirebaseToken()


            //There is a logged in user
            if (loggedInUser.value != null) {

                //Check that the users logged in firebase reg token matches the saved firebase token for the logged in token
                val success = loginRepository.checkFcmRegToken(
                    fcmRegTokenCheckRequest(
                        userId = loggedInUser.value!!.userId,
                        firebaseRegistrationToken = firebaseToken
                    )
                )

                //If they dont, log the user out. Display a message informing the user why they have been logged out
                if (!success.success) {
                    context.toast("The user has logged in from another device, and as a result, they have been logged out of this device.")
                    parentRepository.logoutUser(context)
                    _userAlreadyLoggedIn.value = false
                }
                //Else user is already logged in, continue to next screen
                else {
                    _userAlreadyLoggedIn.value = true
                }
            }
            else{
                _userAlreadyLoggedIn.value = false
            }
        }

    }


}

class SplashActivityViewModelFactory(
    private val loginRepository: LoggedInUserRepository,
    private val parentRepository: ParentRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SplashActivityViewModel(loginRepository, parentRepository) as T

        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}