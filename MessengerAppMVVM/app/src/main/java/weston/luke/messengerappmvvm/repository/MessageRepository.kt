package weston.luke.messengerappmvvm.repository

import android.util.Base64
import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import weston.luke.messengerappmvvm.data.database.dao.MessageDao
import weston.luke.messengerappmvvm.data.database.dto.LatestMessage
import weston.luke.messengerappmvvm.data.database.entities.Message
import weston.luke.messengerappmvvm.data.database.entities.SentStatus
import weston.luke.messengerappmvvm.data.remote.api.MessengerAPIService
import weston.luke.messengerappmvvm.data.remote.request.MessageSendRequest
import java.time.LocalDateTime

class MessageRepository(private val messageDao: MessageDao, private val apiService: MessengerAPIService) {

    @WorkerThread
    suspend fun insertMessages(messages: List<Message>) {
        return messageDao.insertMessages(messages)
    }

    @WorkerThread
    suspend fun insertMessage(message: Message): Long {
        return messageDao.insertMessage(message)
    }

    @WorkerThread
    suspend fun updateMessage(message: Message) {
        messageDao.updateMessage(message)
    }

    @WorkerThread
    suspend fun deleteAllMessages() {
        messageDao.deleteAllMessages()
    }


    @WorkerThread
    fun getLastestMessagesForEachConversation() : Flow<List<LatestMessage?>>{
        return messageDao.getLatestMessagesForEachConversation()
    }

    @WorkerThread
    fun getAllMessagesForAConversation(conversationId: Int): Flow<List<Message>> {
        return messageDao.getAllMessagesForAConversation(conversationId)
    }

    suspend fun getAllMessagesForUser(userId: Int){
        val messageResponse = apiService.getAllMessagesForUser(userId)
        insertMessages(
            messageResponse.map {
                Message(
                    messageId = it.id,
                    userId = it.userId,
                    conversationId = it.conversationId,
                    userName = it.username,
                    message = it.message,
                    timeSent = LocalDateTime.parse(it.timeSent),
                    timeUpdated = if (it.timeUpdated != null) LocalDateTime.parse(it.timeUpdated) else null,
                    status = SentStatus.SUCCESS
                )
            }
        )

    }

    suspend fun sendMessage(message: Message): Boolean{
        //Insert a new message and get its local id
        val messageId = insertMessage(message)

        try {
            //Send the message to the server
            val messageResponse = apiService.sendMessage(
                MessageSendRequest(
                    userId = message.userId,
                    message = message.message,
                    conversationId = message.conversationId,
                    imageBase64 = if(message.image != null) Base64.encodeToString(message.image, Base64.DEFAULT) else null
                )
            )

            //Update the message in the local server as it is successful
            updateMessage(
                Message(
                    id = messageId,
                    messageId = messageResponse.id,
                    userId = messageResponse.userId,
                    conversationId = messageResponse.conversationId,
                    userName = messageResponse.username,
                    message = messageResponse.message,
                    timeSent = LocalDateTime.parse(messageResponse.timeSent),
                    timeUpdated = null,
                    status = SentStatus.SUCCESS,
                )
            )
        }
        catch (e:Exception){
            return false
        }
        return true

    }


}