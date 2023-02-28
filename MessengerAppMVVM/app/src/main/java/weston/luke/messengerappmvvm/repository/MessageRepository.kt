package weston.luke.messengerappmvvm.repository

import androidx.annotation.WorkerThread
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import weston.luke.messengerappmvvm.data.database.dao.ConversationDao
import weston.luke.messengerappmvvm.data.database.dao.MessageDao
import weston.luke.messengerappmvvm.data.database.dto.LatestMessage
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.data.database.entities.Message

class MessageRepository(private val messageDao: MessageDao) {

    @WorkerThread
    suspend fun insertMessages(messages : List<Message>){
        return messageDao.insertMessages(messages)
    }

    @WorkerThread
    suspend fun deleteAllMessages(){
        messageDao.deleteAllMessages()
    }

    @WorkerThread
    suspend fun getLatestMessageForConversation(conversationId: Int): LatestMessage{
        return messageDao.getLatestMessageForConversation(conversationId = conversationId)
    }


}