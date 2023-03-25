package weston.luke.messengerappmvvm.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import weston.luke.messengerappmvvm.data.database.entities.Conversation

@Dao
interface ConversationDao {

    @Query("select * from conversation")
    fun getAllConversations(): LiveData<List<Conversation>>

    @Query("select * from conversation where conversationId = :conversationId")
    fun getConversation(conversationId: Int): Flow<Conversation>

    @Update
    fun updateConversation(conversation: Conversation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<Conversation>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: Conversation)

    @Query("Delete from conversation")
    suspend fun deleteAllConversationData()

    @Query("Delete from conversation where conversationId = " +
            "(select privateConversationId from friend where friendId = :friendId)")
    suspend fun deletePrivateConversationByFriendshipId(friendId: Int)

}