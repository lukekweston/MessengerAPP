package weston.luke.messengerappmvvm.ui.login

import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser
import weston.luke.messengerappmvvm.data.remote.api.MessengerAPIService
import weston.luke.messengerappmvvm.data.remote.request.LoginRequest
import weston.luke.messengerappmvvm.data.remote.response.LoginResponse
import weston.luke.messengerappmvvm.data.remote.response.UserResponse
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import java.lang.IllegalArgumentException

class LoginViewModel(private val repository: LoggedInUserRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val api = MessengerAPIService()

    val loggedInUser: LiveData<LoggedInUser?> = repository.loggedInUser.asLiveData()

    private val _toastMessage = MutableLiveData<String>()

    val toastMessage: LiveData<String>
        get() = _toastMessage

    private val _invalidUserNameOrPassword = MutableLiveData<Boolean>()

    val invalidUserNameOrPassword: LiveData<Boolean>
        get() = _invalidUserNameOrPassword


    private val _loggingUserIn = MutableLiveData<Boolean>()

    val loggingUserIn: LiveData<Boolean>
        get() = _loggingUserIn


    suspend fun checkUserAlreadyLoggedIn(): Boolean{
        return repository.awaitGettingLoggedInUser() != null
    }

    @WorkerThread
    fun addUserToBeLoggedInLocally(loginResponse: LoginResponse) = viewModelScope.launch {
        repository.loginUser(
            LoggedInUser(
                userId = loginResponse.UserId,
                userName = loginResponse.UserName,
                userEmail = loginResponse.UserEmail
            )
        )
    }


    fun loginUser(userName: String, password: String): Boolean {

        var successfulLogin = false

        compositeDisposable.add(
            api.loginUser(LoginRequest(userName = userName, password = password))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<LoginResponse>() {
                    override fun onSuccess(loginResponse: LoginResponse) {
                        if (loginResponse.SuccessfulLogin) {
                            _loggingUserIn.value = true
                            _invalidUserNameOrPassword.value = false
                            _toastMessage.value = "success"
                            addUserToBeLoggedInLocally(loginResponse)

//                            Load conversations to local database

//                            Load messages to local database

//                            Start worker to poll and get messages/conversations

                            successfulLogin = true
                        } else {
                            _toastMessage.value = "fail"
                            _invalidUserNameOrPassword.value = true
                        }

                    }

                    override fun onError(e: Throwable) {
                        _toastMessage.value = "Error logging in, please try again"
                    }
                })
        )
        return successfulLogin
    }

}

class LoginViewModelFactory(private val repository: LoggedInUserRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T

        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}