package weston.luke.messengerappmvvm.application

import android.app.Application
import weston.luke.messengerappmvvm.data.database.MessengerAppMVVMDatabase
import weston.luke.messengerappmvvm.repository.ConversationRepository
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import weston.luke.messengerappmvvm.repository.MessageRepository


class MessengerAppMVVMApplication : Application() {

    private val database by lazy { MessengerAppMVVMDatabase.getDatabase(this@MessengerAppMVVMApplication)}

    val loggedInUserRepository by lazy { LoggedInUserRepository(database.loggedInUserDao()) }
    val conversationRepository by lazy { ConversationRepository(database.conversationDao()) }
    val messageRepository by lazy { MessageRepository(database.messageDao()) }

}