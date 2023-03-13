package weston.luke.messengerappmvvm.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import weston.luke.messengerappmvvm.data.database.entities.Friend
import weston.luke.messengerappmvvm.data.database.entities.FriendshipStatus

@Dao
interface FriendDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: Friend)

    @Query("Select * from friend where friendStatus = :friendshipStatus")
    fun getFriendsByFriendshipStatus(friendshipStatus: FriendshipStatus): LiveData<List<Friend>>

    @Delete
    suspend fun delete(friend: Friend)

    @Query("Delete from friend where friendId = :friendId")
    suspend fun deleteByFriendId(friendId: Int)

    @Query("Delete from friend")
    suspend fun deleteAllFriends()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriends(friend: List<Friend>)

    @Query("update friend set friendStatus = :friendshipStatus, privateConversationId = :conversationId where friendId = :friendId")
    suspend fun updateFriendStatusForFriendId(friendshipStatus: FriendshipStatus, conversationId: Int?, friendId: Int)


}