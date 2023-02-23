package com.messenger.springMessengerAPI.services

import com.messenger.springMessengerAPI.models.Message
import com.messenger.springMessengerAPI.models.request.NewMessageRequest
import com.messenger.springMessengerAPI.models.request.UpdateMessageRequest
import com.messenger.springMessengerAPI.repositories.MessageRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class MessageService(private val messageRepository: MessageRepository, private val conversationService: ConversationService) {
    fun getAllMessagesForConversation(conversationId: Int): List<Message> {
        return messageRepository.findAllByConversationId(conversationId)
    }

    fun getAllMessagesForUser(userId: Int): List<Message> {
        val conversationsUserBelongsToo = conversationService.getConversationsForUser(userId)

        val messages = mutableListOf<Message>()
        for (conversation in conversationsUserBelongsToo) {
            messages += messageRepository.findAllByConversationId(conversationId = conversation.id)
        }
        return messages
    }

    fun getAllMessagesForUserAfterDateTime(userId: Int, dateTime: LocalDateTime): List<Message> {
        return getAllMessagesForUser(userId).filter { it.timeSent > dateTime  || (it.updatedTime != null &&  it.updatedTime> dateTime)}
    }

    fun newMessage(newMessageRequest: NewMessageRequest) {
        messageRepository.save(Message(
                userId = newMessageRequest.userId,
                textMessage = newMessageRequest.message,
                timeSent = LocalDateTime.now(),
                conversationId = newMessageRequest.conversationId))
    }

    fun updateMessage(messageToUpdate: UpdateMessageRequest){
        val messageOld = messageRepository.findById(messageToUpdate.messageId)
        if(messageOld.isPresent){
            messageRepository.save( Message(
                    //New values
                    textMessage = messageToUpdate.message,
                    updatedTime = LocalDateTime.now(),
                    //Keep the rest the same
                    id = messageOld.get().id,
                    userId = messageOld.get().userId,
                    timeSent = messageOld.get().timeSent,
                    conversationId = messageOld.get().conversationId))
        }
    }

    fun deleteMessage(messageId: Int){
        messageRepository.deleteById(messageId)
    }
}