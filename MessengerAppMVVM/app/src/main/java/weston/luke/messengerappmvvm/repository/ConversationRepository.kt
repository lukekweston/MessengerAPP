package weston.luke.messengerappmvvm.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import weston.luke.messengerappmvvm.data.database.dao.ConversationDao
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.data.remote.api.MessengerAPIService
import java.time.LocalDateTime

class ConversationRepository(private val conversationDao: ConversationDao, private val apiService: MessengerAPIService) {

   private val conversations: LiveData<List<Conversation>> = conversationDao.getAllConversations()

    fun getConversations(): LiveData<List<Conversation>>{
        return conversations
    }

    @WorkerThread
    fun getConversation(conversationId: Int): Flow<Conversation>{
        return conversationDao.getConversation(conversationId)
    }

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

    suspend fun getAllConversationsForUser(userId: Int){
        //Get all the conversations for a user from the api
        val conversationResponse = apiService.getAllConversationsForUser(userId)
        //Insert them into the database
        insertConversations(
            conversationResponse.map {
                Conversation(
                    conversationId = it.id,
                    conversationName = it.conversationName,
                    lastUpdatedDateTime = if (it.lastUpdated != null) LocalDateTime.parse(it.lastUpdated) else null

                )
            }
        )
    }


}