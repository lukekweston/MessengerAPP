package weston.luke.messengerappmvvm.ui.messages

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser
import weston.luke.messengerappmvvm.data.database.entities.Message
import weston.luke.messengerappmvvm.data.database.entities.MessageStatus
import weston.luke.messengerappmvvm.repository.ConversationRepository
import weston.luke.messengerappmvvm.repository.LoggedInUserRepository
import weston.luke.messengerappmvvm.repository.MessageRepository
import weston.luke.messengerappmvvm.util.ImageUtils
import java.time.LocalDateTime

class MessagesViewModel(
    private val loggedInUserRepository: LoggedInUserRepository,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {


    private val _toastMessageToDisplay =
        MutableLiveData<String>()

    val toastMessageToDisplay: LiveData<String>
        get() = _toastMessageToDisplay

    val loggedInUser = MutableLiveData<LoggedInUser>()

    val messages = MutableLiveData<List<Message>>()
    private val loggedInUserId = MutableLiveData<Int>()

    val loggedInUserAndMessages = MediatorLiveData<Pair<List<Message>, Int>>()

    init {
        loggedInUserAndMessages.addSource(messages) {
            loggedInUserAndMessages.value = Pair(it, loggedInUserId.value ?: 0)
        }
        loggedInUserAndMessages.addSource(loggedInUserId) {
            loggedInUserAndMessages.value = Pair(messages.value.orEmpty(), it)
        }
    }


    fun getConversation(conversationId: Int): Flow<Conversation> {
        return conversationRepository.getConversation(conversationId)
    }

    fun loadData(conversationId: Int, context: Context) {
        viewModelScope.launch {
            loggedInUserRepository.loggedInUser.collect {
                loggedInUser.value = it
                loggedInUserId.value = it?.userId
            }
        }
        viewModelScope.launch {
            messageRepository.getAllMessagesForAConversation(conversationId).collect { it ->
                val messagesThatNeedImageDownloaded =
                    it.filter { message -> message.status == MessageStatus.IMAGE_READY_TO_GET_FROM_API }
                if (messagesThatNeedImageDownloaded.isNotEmpty()) {
                    downloadLowResImageForImages(messagesThatNeedImageDownloaded,context)
                }
                messages.value = it
            }
        }
    }


    private suspend fun downloadLowResImageForImages(messages: List<Message>, context: Context) {
        for (message in messages) {
            messageRepository.getLowResImageForMessage(message, context)
        }
    }

    fun sendMessage(textMessage: String, conversationId: Int) {
        viewModelScope.launch {
            val message = Message(
                conversationId = conversationId,
                message = textMessage,
                userId = loggedInUser.value!!.userId,
                userName = loggedInUser.value!!.userName,
                status = MessageStatus.CREATED,
                timeSent = LocalDateTime.now(),
                //Todo save the image in a file and put a link to the file
                //Dont save the full res image in the database,
                //imageLowRes = imageBase64String
            )
            val success = messageRepository.sendMessageText(message)

            if (!success) {
                _toastMessageToDisplay.value = "Error contacting the server"
            }
        }
    }

    fun sendImage(
        conversationId: Int,
        imageBase64StringFullRes: String,
        fullResImagePath: String,
        context: Context
    ) {
        viewModelScope.launch {
            val message = Message(
                conversationId = conversationId,
                message = "",
                userId = loggedInUser.value!!.userId,
                userName = loggedInUser.value!!.userName,
                status = MessageStatus.CREATED,
                timeSent = LocalDateTime.now(),
                pathToSavedHighRes = fullResImagePath
            )
            //Save the inital data, this will update the ui
            val messageId = messageRepository.insertMessage(message)

            //Compress the image and then save it and return the directory
            val imageBase64StringLowRes = ImageUtils.resizeImage(imageBase64StringFullRes)
            val pathToLowResImage = ImageUtils.saveImage(context, imageBase64StringLowRes, false)

            message.pathToSavedLowRes = pathToLowResImage
            message.id = messageId

            //Update the message to link it with the low res image
            messageRepository.updateMessage(message)

            //Send the image to the server
            messageRepository.sendMessageImage(
                messageId,
                message,
                imageBase64StringFullRes,
                imageBase64StringLowRes
            )

        }

    }


}

class MessagesViewModelFactory(
    private val loginRepository: LoggedInUserRepository,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessagesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MessagesViewModel(
                loginRepository,
                conversationRepository,
                messageRepository
            ) as T

        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}

