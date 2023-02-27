package weston.luke.messengerappmvvm.repository

import androidx.annotation.WorkerThread
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import weston.luke.messengerappmvvm.data.database.dao.ConversationDao
import weston.luke.messengerappmvvm.data.database.entities.Conversation

class ConversationRepository(private val conversationDao: ConversationDao) {

    val conversations: Flow<List<Conversation>> = conversationDao.getAllConversations()

    @WorkerThread
    fun updateConversation(conversation: Conversation){
        return conversationDao.updateConversation(conversation)
    }

    @WorkerThread
    suspend fun insertConversations(conversations : List<Conversation>){
        return conversationDao.insertConversations(conversations)
    }

    @WorkerThread
    suspend fun deleteConversationData(){
        conversationDao.deleteAllConversationData()
    }


}