package weston.luke.messengerappmvvm.data.database.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "friend")
data class Friend(
    @PrimaryKey val friendId: Int,
    @ColumnInfo val friendUserName: String,
    @ColumnInfo val friendStatus: FriendshipStatus,
) :Parcelable


enum class FriendshipStatus{
    Sent, Pending, Friends, Blocked, Declined, Removed, Unknown
}
