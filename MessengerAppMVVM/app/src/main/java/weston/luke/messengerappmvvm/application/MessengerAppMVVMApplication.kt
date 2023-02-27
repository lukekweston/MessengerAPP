package weston.luke.messengerappmvvm.application

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import weston.luke.messengerappmvvm.data.database.MessengerAppMVVMDatabase
import weston.luke.messengerappmvvm.data.remote.api.messengerAPIInterface
import weston.luke.messengerappmvvm.repository.ConversationsRepository
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import weston.luke.messengerappmvvm.util.Constants


class MessengerAppMVVMApplication : Application() {

    private val database by lazy { MessengerAppMVVMDatabase.getDatabase(this@MessengerAppMVVMApplication)}

    val loggedInUserRepository by lazy { LoggedInUserRepository(database.loggedInUserDao()) }
    val conversationsRepository by lazy { ConversationsRepository(database.conversationDao()) }

}