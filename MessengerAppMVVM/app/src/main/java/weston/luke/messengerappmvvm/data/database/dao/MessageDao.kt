package weston.luke.messengerappmvvm.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import weston.luke.messengerappmvvm.data.database.entities.Conversation
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser
import weston.luke.messengerappmvvm.data.database.entities.Message

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<Message>)

    @Query("Delete from message")
    suspend fun deleteAllMessages()

    @Query("SELECT TOP(1) UserId, TextMessage, GREATEST(TimeSent, UpdatedTime) AS max_value\n" +
            "FROM messages\n" +
            "WHERE ConversationId = 1\n" +
            "ORDER BY max_value DESC;")

}