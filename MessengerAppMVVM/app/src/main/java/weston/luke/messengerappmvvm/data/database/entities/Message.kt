package weston.luke.messengerappmvvm.data.database.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime


@Parcelize
@Entity(tableName = "message")
data class Message(
    //Duplicate id here, message id and id as messages will be created in room database before being
    //posted to database (for holding created messages if the post fails)
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val messageId: Int,
    @ColumnInfo val userId: Int,
    @ColumnInfo val conversationId: Int,
    @ColumnInfo val userName: String,
    @ColumnInfo val message: String,
    @ColumnInfo val timeSent: LocalDateTime,
    @ColumnInfo val timeUpdated: LocalDateTime?,
    @ColumnInfo val status: SentStatus
) : Parcelable


enum class SentStatus {
    RECEIVED_FROM_API,
    CREATED_LOCALLY,
    CREATED_AND_SENT
}