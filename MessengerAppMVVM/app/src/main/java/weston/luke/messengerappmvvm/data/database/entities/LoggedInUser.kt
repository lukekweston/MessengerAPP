package weston.luke.messengerappmvvm.data.database.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "logged_in_user")
data class LoggedInUser(
    @ColumnInfo val userId: Int,
    @ColumnInfo val userName: String,
    @ColumnInfo val userEmail: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
): Parcelable
