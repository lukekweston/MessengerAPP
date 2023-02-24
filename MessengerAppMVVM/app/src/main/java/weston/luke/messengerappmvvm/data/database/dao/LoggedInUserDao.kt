package weston.luke.messengerappmvvm.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import weston.luke.messengerappmvvm.data.database.entities.LoggedInUser

@Dao
interface LoggedInUserDao {

    @Insert
    suspend fun logInUser(user: LoggedInUser)

    @Query("Delete from LOGGED_IN_USER")
    suspend fun logoutUser()

    @Query("select * from LOGGED_IN_USER Limit 1")
    suspend fun getLoggedInUser() : LoggedInUser
}