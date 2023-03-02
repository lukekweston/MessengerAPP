package weston.luke.messengerappmvvm.application

import android.app.Application
import weston.luke.messengerappmvvm.data.database.MessengerAppMVVMDatabase
import weston.luke.messengerappmvvm.data.remote.api.MessengerAPIService
import weston.luke.messengerappmvvm.repository.ConversationRepository
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import weston.luke.messengerappmvvm.repository.MessageRepository


class MessengerAppMVVMApplication : Application() {

    private val database by lazy { MessengerAppMVVMDatabase.getDatabase(this@MessengerAppMVVMApplication)}

    val loggedInUserRepository by lazy { LoggedInUserRepository(database.loggedInUserDao(), MessengerAPIService()) }
    val conversationRepository by lazy { ConversationRepository(database.conversationDao(), MessengerAPIService()) }
    val messageRepository by lazy { MessageRepository(database.messageDao(), MessengerAPIService()) }

}