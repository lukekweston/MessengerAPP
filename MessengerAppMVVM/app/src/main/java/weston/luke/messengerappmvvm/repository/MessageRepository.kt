package weston.luke.messengerappmvvm.repository

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import weston.luke.messengerappmvvm.data.database.dao.MessageDao
import weston.luke.messengerappmvvm.data.database.dto.LatestMessage
import weston.luke.messengerappmvvm.data.database.entities.Message
import weston.luke.messengerappmvvm.data.database.entities.SentStatus
import weston.luke.messengerappmvvm.data.remote.api.MessengerAPIService
import weston.luke.messengerappmvvm.data.remote.request.MessageSendRequest
import java.time.LocalDateTime

class MessageRepository(private val messageDao: MessageDao, private val api: MessengerAPIService) {

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
    suspend fun getLatestMessageForConversation(conversationId: Int): LatestMessage {
        return messageDao.getLatestMessageForConversation(conversationId = conversationId)
    }

    @WorkerThread
    fun getAllMessagesForAConversation(conversationId: Int): Flow<List<Message>> {
        return messageDao.getAllMessagesForAConversation(conversationId)
    }

    suspend fun sendMessage(message: Message): Boolean{
        //Insert a new message and get its local id
        val messageId = insertMessage(message)

        try {
            //Send the message to the server
            val messageResponse = api.sendMessage(
                MessageSendRequest(
                    userId = message.userId,
                    message = message.message,
                    conversationId = message.conversationId
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