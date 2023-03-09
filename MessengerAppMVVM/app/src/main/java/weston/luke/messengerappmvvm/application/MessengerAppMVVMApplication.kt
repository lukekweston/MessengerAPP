package weston.luke.messengerappmvvm.application

import android.app.Application
import weston.luke.messengerappmvvm.data.database.MessengerAppMVVMDatabase
import weston.luke.messengerappmvvm.data.remote.api.MessengerAPIService
import weston.luke.messengerappmvvm.repository.ConversationRepository
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import weston.luke.messengerappmvvm.repository.MessageRepository
import weston.luke.messengerappmvvm.repository.ParentRepository


class MessengerAppMVVMApplication : Application() {

    val database by lazy { MessengerAppMVVMDatabase.getDatabase(this@MessengerAppMVVMApplication)}
//    val database = Room.databaseBuilder(applicationContext, MessengerAppMVVMDatabase::class.java, "messenger-db").build()


    val conversationRepository by lazy { ConversationRepository(database.conversationDao(), MessengerAPIService(this)) }
    val loggedInUserRepository by lazy { LoggedInUserRepository(database.loggedInUserDao(), MessengerAPIService(this)) }
    val messageRepository by lazy { MessageRepository(database.messageDao(), MessengerAPIService(this)) }

    val parentRepository by lazy{ ParentRepository(conversationRepository, loggedInUserRepository, messageRepository) }


}