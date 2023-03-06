package weston.luke.messengerappmvvm.repository

import androidx.annotation.WorkerThread
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.Flow
import weston.luke.messengerappmvvm.data.database.dao.LoggedInUserDao
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser
import weston.luke.messengerappmvvm.data.remote.api.MessengerAPIService
import weston.luke.messengerappmvvm.data.remote.request.LoginRequest
import weston.luke.messengerappmvvm.data.remote.request.LogoutRequest
import weston.luke.messengerappmvvm.data.remote.request.fcmRegTokenCheckRequest
import weston.luke.messengerappmvvm.data.remote.response.LoginResponse
import weston.luke.messengerappmvvm.data.remote.response.SuccessResponse
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LoggedInUserRepository(private val loggedInUserDao: LoggedInUserDao, private val apiService: MessengerAPIService) {
    @WorkerThread
    suspend fun loginUser(user: LoggedInUser){
        loggedInUserDao.loginUser(user)
    }

    @WorkerThread
    suspend fun deleteUserFromLocalDatabase(){
        loggedInUserDao.logoutUser()
    }

    val loggedInUser: Flow<LoggedInUser?> = loggedInUserDao.getLoggedInUser()

    @WorkerThread
    suspend fun awaitGettingLoggedInUser() : LoggedInUser? {
        return loggedInUserDao.awaitGetLoggedInUser()
    }

    suspend fun loginUser(loginRequest: LoginRequest): LoginResponse {
        return apiService.loginUser(loginRequest)
    }

    suspend fun logoutUser(logoutRequest: LogoutRequest): SuccessResponse {
        return apiService.logoutUser(logoutRequest)
    }

    suspend fun checkFcmRegToken(fcmRegTokenCheckRequest: fcmRegTokenCheckRequest) : SuccessResponse{
        return apiService.checkFcmRegToken(fcmRegTokenCheckRequest)
    }


    suspend fun getFirebaseToken(): String = suspendCoroutine { continuation ->
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    continuation.resume(token!!)
                } else {
                    continuation.resumeWithException(task.exception!!)
                }
            }
    }

}