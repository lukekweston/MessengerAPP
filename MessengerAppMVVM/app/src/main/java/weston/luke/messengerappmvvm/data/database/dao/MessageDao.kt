package weston.luke.messengerappmvvm.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import weston.luke.messengerappmvvm.data.database.dto.LatestMessage
import weston.luke.messengerappmvvm.data.database.entities.Message

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<Message>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message): Long

    @Update
    suspend fun updateMessage(message: Message)

    @Query("Delete from message")
    suspend fun deleteAllMessages()

    @Query("SELECT id, userName, message, MAX(CASE WHEN timeSent > timeUpdated THEN timeSent ELSE timeUpdated END) AS latestTime " +
            "FROM message " +
            "WHERE conversationId = :conversationId " +
            "ORDER BY latestTime DESC " +
            "LIMIT 1;")
    suspend fun getLatestMessageForConversation(conversationId: Int) : LatestMessage

    @Query("select * from message where conversationId = :conversationId")
    fun getAllMessagesForAConversation(conversationId: Int): Flow<List<Message>>

}