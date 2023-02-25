package weston.luke.messengerappmvvm.application

import android.app.Application
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import weston.luke.messengerappmvvm.data.database.MessengerAppMVVMDatabase
import weston.luke.messengerappmvvm.data.remote.api.messengerAPIInterface
import weston.luke.messengerappmvvm.data.remote.response.UserResponse
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import weston.luke.messengerappmvvm.util.Constants


class MessengerAppMVVMApplication : Application() {

    private val database by lazy { MessengerAppMVVMDatabase.getDatabase(this@MessengerAppMVVMApplication)}

    val repository by lazy { LoggedInUserRepository(database.loggedInUserDao()) }






    object RetrofitClient {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        val messengerApi: messengerAPIInterface by lazy {
            retrofit.create(messengerAPIInterface::class.java)
        }
    }
}