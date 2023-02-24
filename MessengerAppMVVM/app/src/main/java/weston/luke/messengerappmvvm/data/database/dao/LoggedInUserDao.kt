package weston.luke.messengerappmvvm.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser

@Dao
interface LoggedInUserDao {

    @Insert
    suspend fun loginUser(user: LoggedInUser)

    @Query("Delete from logged_in_user")
    suspend fun logoutUser()

    @Query("select * from logged_in_user Limit 1")
    fun getLoggedInUser() : Flow<LoggedInUser?>
}