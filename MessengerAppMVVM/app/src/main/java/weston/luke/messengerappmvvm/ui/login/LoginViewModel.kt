package weston.luke.messengerappmvvm.ui.login

import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser
import weston.luke.messengerappmvvm.data.repository.LoggedInUserRepository
import java.lang.IllegalArgumentException

class LoginViewModel(private val repository: LoggedInUserRepository) : ViewModel() {

    val loggedInUser: LiveData<LoggedInUser?> = repository.loggedInUser.asLiveData()


    @WorkerThread
    fun loginUser() = viewModelScope.launch {
        repository.loginUser(LoggedInUser(userId = 1, userName = "Luke", userEmail = "hello@gmail.com"))
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