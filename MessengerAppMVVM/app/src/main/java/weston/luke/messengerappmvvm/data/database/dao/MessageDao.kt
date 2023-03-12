package weston.luke.messengerappmvvm.data.database.dao

import androidx.lifecycle.LiveData
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



    @Query("""
            SELECT m.id, m.conversationId, m.message, m.userName, m.pathToSavedLowRes, MAX(m.timeSent, COALESCE(m.timeUpdated, m.timeSent)) AS latestTime 
            FROM message m 
            INNER JOIN (
                SELECT conversationId, MAX(MAX(timeSent, COALESCE(timeUpdated, timeSent))) AS max_latest_time 
                FROM message 
                GROUP BY conversationId 
            ) t ON m.conversationId = t.conversationId AND MAX(m.timeSent, COALESCE(m.timeUpdated, m.timeSent)) = t.max_latest_time;"""
    )
    fun getLatestMessagesForEachConversation(): LiveData<List<LatestMessage?>>

    @Query("select * from message where conversationId = :conversationId order by messageId asc")
    fun getAllMessagesForAConversation(conversationId: Int): Flow<List<Message>>

}