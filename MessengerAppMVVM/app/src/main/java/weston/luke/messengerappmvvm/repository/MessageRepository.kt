package weston.luke.messengerappmvvm.repository

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import weston.luke.messengerappmvvm.data.database.dao.MessageDao
import weston.luke.messengerappmvvm.data.database.dto.LatestMessage
import weston.luke.messengerappmvvm.data.database.entities.Message
import weston.luke.messengerappmvvm.data.database.entities.MessageStatus
import weston.luke.messengerappmvvm.data.remote.api.MessengerAPIService
import weston.luke.messengerappmvvm.data.remote.request.MessageSendRequest
import weston.luke.messengerappmvvm.util.ImageUtils
import java.time.LocalDateTime

class MessageRepository(
    private val messageDao: MessageDao,
    private val apiService: MessengerAPIService
) {

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
    fun getLastestMessagesForEachConversation(): Flow<List<LatestMessage?>> {
        return messageDao.getLatestMessagesForEachConversation()
    }

    @WorkerThread
    fun getAllMessagesForAConversation(conversationId: Int): Flow<List<Message>> {
        return messageDao.getAllMessagesForAConversation(conversationId)
    }

    suspend fun getAllMessagesForUser(userId: Int, context: Context) {
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
                    status = MessageStatus.SUCCESS,
                    pathToSavedLowRes = if (it.imageLowRes != null) ImageUtils.saveImage(
                        context,
                        it.imageLowRes,
                        false,
                        it.id.toString()
                    ) else ""
                )
            }
        )

    }

    //Todo, fix the send by saving the photo in a file on the phone before sending it
    //Image is too large for the database (it is bad practise to put large files in a database)
    //Also low res photos should be saved outside of the database
    suspend fun sendMessageText(message: Message): Boolean {
        return try {
            val messageId = insertMessage(message)
            sendMessage(message, messageId = messageId)
        } catch (e: Exception) {
            Log.e("Error saving message", e.message.toString())
            false
        }
    }

    suspend fun sendMessageImage(
        messageId: Long,
        message: Message,
        imageBase64StringFullRes: String,
        imageBase64StringLowRes: String
    ): Boolean {
        return sendMessage(message, messageId, imageBase64StringFullRes, imageBase64StringLowRes)
    }


    suspend fun sendMessage(
        message: Message,
        messageId: Long,
        imageBase64StringFullRes: String? = null,
        imageBase64StringLowRes: String? = null
    ): Boolean {
        //Insert a new message and get its local id
        try {
            //Send the message to the server
            val messageResponse = apiService.sendMessage(
                MessageSendRequest(
                    userId = message.userId,
                    message = message.message,
                    conversationId = message.conversationId,
                    imageBase64FullRes = imageBase64StringFullRes,
                    imageBase64LowRes = imageBase64StringLowRes
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
                    status = if (messageResponse.imageLowRes == null) MessageStatus.SUCCESS else MessageStatus.SUCCESS_LOW_RES_IMAGE_ONLY,
                    pathToSavedLowRes = message.pathToSavedLowRes
                )
            )
        } catch (e: Exception) {
            Log.e("Error sending message", e.message.toString())
            return false
        }
        return true

    }

    //Gets the image from the server and saves it back on the image, updates status flag
    suspend fun getLowResImageForMessage(message: Message, context: Context) {
        //MessageId will have to have a value to get here - but still do null check
        if (message.messageId != null) {
            val imageResponse = apiService.getLowResImageForMessage(message.messageId)
            if (imageResponse.imageBase64.isNotEmpty()) {

                //Save the image and return its path and set the path to saved low res image to this
                message.pathToSavedLowRes = ImageUtils.saveImage(
                    context,
                    imageResponse.imageBase64,
                    false,
                    imageResponse.messageId.toString()
                )
                message.status = MessageStatus.SUCCESS_LOW_RES_IMAGE_ONLY
                updateMessage(message)
            }

        }
    }


}