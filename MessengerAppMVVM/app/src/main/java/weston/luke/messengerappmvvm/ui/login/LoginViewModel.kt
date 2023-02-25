package weston.luke.messengerappmvvm.ui.login

import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser
import weston.luke.messengerappmvvm.data.remote.api.MessengerAPIService
import weston.luke.messengerappmvvm.data.remote.response.UserResponse
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import java.lang.IllegalArgumentException

class LoginViewModel(private val repository: LoggedInUserRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val api = MessengerAPIService()


    val loggedInUser: LiveData<LoggedInUser?> = repository.loggedInUser.asLiveData()



    @WorkerThread
    fun loginUser() = viewModelScope.launch {
        repository.loginUser(LoggedInUser(userId = 1, userName = "Luke", userEmail = "hello@gmail.com"))
    }

    fun getAllUsers(){
        compositeDisposable.add(
            api.getAllUsers().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<UserResponse>>() {
                    override fun onSuccess(value: List<UserResponse>) {
                        value
                    }

                    override fun onError(e: Throwable) {
                        e
                    }
                })
        )
    }

}

class LoginViewModelFactory(private val repository: LoggedInUserRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LoginViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T

        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}