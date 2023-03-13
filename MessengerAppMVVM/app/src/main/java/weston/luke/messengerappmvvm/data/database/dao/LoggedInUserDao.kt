package weston.luke.messengerappmvvm.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser

@Dao
interface LoggedInUserDao {

    @Insert
    suspend fun loginUser(user: LoggedInUser)

    @Query("Delete from logged_in_user")
    suspend fun logoutUser()

    @Query("select * from logged_in_user Limit 1")
    fun getLoggedInUser() : LiveData<LoggedInUser?>
}